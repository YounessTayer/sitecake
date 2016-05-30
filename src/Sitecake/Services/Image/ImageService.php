<?php

namespace Sitecake\Services\Image;

use Sitecake\Exception\Http\BadRequestException;
use Sitecake\Util\Utils;
use Sitecake\Services\Service;
use WideImage\WideImage;

class ImageService extends Service
{
	protected static $_imageExtensions = ['jpg', 'jpeg', 'png', 'gif'];

	protected $_ctx;

	public function __construct($ctx)
	{
		$this->_ctx = $ctx;
	}

    /**
     * Upload service
     *
     * @param $request
     *
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
	public function upload($request)
	{

		// obtain the uploaded file, load image and get its details (filename, extension)
		if (!$request->headers->has('x-filename'))
		{
            throw new BadRequestException('Filename is missing (header X-FILENAME)');
		}
		$filename = base64_decode($request->headers->get('x-filename'));
		$pathinfo = pathinfo($filename);

		if (!in_array(strtolower($pathinfo['extension']), self::$_imageExtensions))
		{
			return $this->json($request, [
				'status' => 1,
				'errMessage' => "$filename is not an image file"
			], 200);
		}

		$filename = Utils::sanitizeFilename($pathinfo['filename']);
		$ext = $pathinfo['extension'];
		$img = WideImage::load("php://input");;

		// generate image set
		$res = $this->_generateImageSet($img, $filename, $ext);

		$res = [
			'status' => 0,
			'srcset' => $res['srcset'],
			'ratio' => $res['ratio']
		];

		return $this->json($request, $res, 200);
	}

    /**
     * External upload service
     *
     * @param $request
     *
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
	public function uploadExternal($request)
	{
		if (!$request->request->has('src'))
		{
            throw new BadRequestException('Image URI is missing');
		}

		$uri = $request->request->get('src');
		$referer = substr($uri, 0, strrpos($uri, '/'));
		$ch = curl_init($uri);
		curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_setopt($ch, CURLOPT_REFERER, $referer);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_BINARYTRANSFER, 1);
		$output = curl_exec($ch);
		curl_close($ch);

		try
		{
			$img = WideImage::loadFromString($output);
		}
		catch (\Exception $e)
		{
			throw new BadRequestException(sprintf('Unable to load image from %s (referer: %s)', $uri, $referer));
		}
		unset($output);

		$urlinfo = parse_url($uri);
		$pathinfo = pathinfo($urlinfo['path']);
		$filename = $pathinfo['filename'];
		$ext = $pathinfo['extension'];

		// generate image set
		$res = $this->_generateImageSet($img, $filename, $ext);

		$res = [
			'status' => 0,
			'srcset' => $res['srcset'],
			'ratio' => $res['ratio']
		];

		return $this->json($request, $res, 200);
	}

    /**
     * Transform image service
     *
     * @param $request
     *
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
	public function image($request)
	{
		if (!$request->request->has('image'))
		{
            throw new BadRequestException('Image URI is missing');
		}
		$uri = $request->request->get('image');

		if (!$request->request->has('data'))
		{
            throw new BadRequestException('Image transformation data is missing');
		}
		$data = $request->request->get('data');

		if (!$this->_ctx['fs']->has($uri))
		{
            throw new BadRequestException(sprintf('Source image not found (%s)', $uri));
		}
		$img = WideImage::loadFromString($this->_ctx['fs']->read($uri));

		if (Utils::isScResourceUrl($uri))
		{
			$info = Utils::resurlinfo($uri);
		}
		else
		{
			$pathinfo = pathinfo($uri);
			$info = ['name' => $pathinfo['filename'], 'ext' => $pathinfo['extension']];
		}

		$datas = explode(':', $data);
		$left = $datas[0];
		$top = $datas[1];
		$width = $datas[2];
		$height = $datas[3];
		$filename = $info['name'];
		$ext = $info['ext'];

		$img = $this->_transformImage($img, $top, $left, $width, $height);

		// generate image set
		$res = $this->_generateImageSet($img, $filename, $ext);

		$res = [
			'status' => 0,
			'srcset' => $res['srcset'],
			'ratio' => $res['ratio']
		];

		return $this->json($request, $res, 200);
	}

    /**
     * Generates different image sizes for passed image based on defined 'image.srcset_widths' and
     * 'image.srcset_width_maxdiff' values from configuration
     *
     * @param \WideImage\Image $img
     * @param string $filename
     * @param string $ext
     *
     * @return array Array of generated images information and images ratio
     *               Images information contains its width, height and url (relative path)
     */
	protected function _generateImageSet($img, $filename, $ext)
	{
		$width = $img->getWidth();
		$ratio = $width / $img->getHeight();

		$widths = $this->_ctx['image.srcset_widths'];
		$maxDiff = $this->_ctx['image.srcset_width_maxdiff'];
		rsort($widths);

		$maxWidth = $widths[0];
		if ($width > $maxWidth)
		{
			$width = $maxWidth;
		}

		$id = uniqid();

		$srcset = [];
		foreach ($this->__neededWidths($width, $widths, $maxDiff) as $targetWidth)
		{
			$tpath = Utils::resurl($this->__imgDir(), $filename, $id, '-' . $targetWidth, $ext);
			$timg = $img->resize($targetWidth);
			$targetHeight = $timg->getHeight();
			$this->_ctx['fs']->write($tpath, $timg->asString($ext));
			$this->_ctx['site']->saveLastModified($tpath);
			unset($timg);
			array_push($srcset, ['width' => $targetWidth, 'height' => $targetHeight, 'url' => $tpath]);
		}

		return ['srcset' => $srcset, 'ratio' => $ratio];
	}

    /**
     * Wrapper method for WideImage\Image::crop method
     *
     * @param \WideImage\Image $img
     * @param float $top
     * @param float $left
     * @param float $width
     * @param float $height
     *
     * @return \WideImage\Image
     */
    protected function _transformImage($img, $top, $left, $width, $height)
    {
        return $img->crop($left . '%', $top . '%', $width . '%', $height . '%');
    }

    /**
     * Returns array of widths base on starting (maximum) width, list of possible widths and
     * maximum difference (in percents) between two image widths in pixels so they could be considered similar
     *
     * @param float $startWidth
     * @param array $widths
     * @param float $maxDiff
     *
     * @return array
     */
	private function __neededWidths($startWidth, $widths, $maxDiff)
	{
		$res = [$startWidth];
		rsort($widths);
		$first = true;
		foreach ($widths as $i => $width)
		{
			if (!$first || ($first && ($startWidth - $width) / $startWidth > $maxDiff / 100))
			{
				array_push($res, $width);
				$first = false;
			}
		}

		return $res;
	}

    /**
     * Returns image draft directory path
     *
     * @return string
     */
	private function __imgDir()
	{
		return $this->_ctx['site']->draftPath() . '/images';
	}
}