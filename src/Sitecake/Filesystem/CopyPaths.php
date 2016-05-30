<?php
namespace Sitecake\Filesystem;

use League\Flysystem\FilesystemInterface;
use League\Flysystem\PluginInterface;

class CopyPaths implements PluginInterface
{
    /**
     * @var FilesystemInterface
     */
    protected $fs;

    public function setFilesystem(FilesystemInterface $filesystem) {
        $this->fs = $filesystem;
    }

    public function getMethod() {
        return 'copyPaths';
    }

    /**
     * Copies the given list of file paths, relative to the
     * given source path, to the given destination path.
     * 
     * @param  array $paths a list of paths to be copied.
     * @param  string $source the source path
     * @param  string $destination the destination path
     * @param  callable|null $callback Optional. If passed should decide weather file should be copied
     */
    public function handle($paths, $source, $destination, $callback = null)
    {
        foreach ($paths as $path)
        {
            $metadata = $this->fs->getMetadata($path);

            if($metadata['type'] == 'file' && (!$callback || $callback($path)))
            {
                $destFile = substr($path, strlen($source));
                $newPath = $destination . '/' .
                           (strpos($destFile, '/') === 0 ? substr($destFile, 1) : $destFile);
                if(!$this->fs->has($newPath))
                {
                    $this->fs->copy($path, $newPath);
                }
                else
                {
                    $this->fs->update($newPath, $this->fs->read($path));
                }
            }
        }
    }
}