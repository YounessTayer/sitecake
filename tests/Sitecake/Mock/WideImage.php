<?php

namespace WideImage;

use WideImage\Exception\InvalidImageHandleException;
use WideImage\Exception\InvalidImageSourceException;

class WideImage
{
    /**
     * Mocks loading uploaded image from php://input in ImageService::upload
     *
     * @param mixed $source
     *
     * @return \WideImage\Image|\WideImage\PaletteImage|\WideImage\TrueColorImage
     */
    public static function load($source)
    {
        $data   = file_get_contents(__DIR__ . '/../../test-content/images/dummy-1540x866-commodore64-plain.jpg');
        $handle = @imagecreatefromstring($data);

        if (imageistruecolor($handle)) {
            return new TrueColorImage($handle);
        }

        return new PaletteImage($handle);
    }

    /**
     * Mocks loading image in ImageService::uploadExternal and ImageService::image
     *
     * @param $string
     *
     * @return PaletteImage|TrueColorImage
     */
    public static function loadFromString($string)
    {
        if (strlen($string) < 128)
        {
            throw new InvalidImageSourceException("String doesn't contain image data.");
        }

        $handle = @imagecreatefromstring($string);

        if (imageistruecolor($handle)) {
            return new TrueColorImage($handle);
        }

        return new PaletteImage($handle);
    }

    public static function isValidImageHandle($handle)
    {
        return (is_resource($handle) && get_resource_type($handle) == 'gd');
    }

    public static function assertValidImageHandle($handle)
    {
        if (!static::isValidImageHandle($handle)) {
            throw new InvalidImageHandleException("{$handle} is not a valid image handle.");
        }
    }
}