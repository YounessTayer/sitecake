<?php

namespace Sitecake\Services\Upload;

use League\Flysystem\FilesystemInterface;
use Sitecake\Exception\Http\BadRequestException;
use Sitecake\Services\Service;
use Sitecake\Util\Utils;

class UploadService extends Service
{
	protected static $forbidden = ['php', 'php5', 'php4', 'php3', 'phtml', 'phpt'];

	/**
	 * @var FilesystemInterface
	 */
	protected $fs;
	/**
	 * @var \Sitecake\Site
	 */
	protected $site;

	public function __construct($ctx)
	{
		$this->fs = $ctx['fs'];
		$this->site = $ctx['site'];
	}

	public function upload($request)
	{
		if (!$request->headers->has('x-filename'))
		{
			throw new BadRequestException('Filename is missing (header X-FILENAME)');
		}
		$filename = base64_decode($request->headers->get('x-filename'));
		$pathinfo = pathinfo($filename);
		$dpath = Utils::resurl($this->site->draftPath() . '/files',
			Utils::sanitizeFilename($pathinfo['filename']), null, null, $pathinfo['extension']);

		if (!$this->_isSafeExtension($pathinfo['extension']))
		{
			return $this->json($request, [
				'status' => 1,
				'errMessage' => 'Forbidden file extension ' . $pathinfo['extension']
			], 200);
		}

		$res = $this->fs->writeStream($dpath, fopen("php://input", 'r'));

		if ($res === false)
		{
			return $this->json($request, [
				'status' => 1,
				'errMessage' => 'Unable to upload file ' . $pathinfo['filename'] . '.' . $pathinfo['extension']
			], 200);
		}
		else
		{
            $this->site->saveLastModified($dpath);
            
			return $this->json($request, ['status' => 0, 'url' => $dpath], 200);
		}
	}

	protected function _isSafeExtension($ext)
	{
		return !in_array(strtolower($ext), self::$forbidden);
	}
}