<?php

namespace Sitecake;

use League\Flysystem\Filesystem;

class FileLock
{
    /**
     * @var Filesystem
     */
    protected $_fs;

    /**
     * Path to tmp dir where lock file is stored
     * @var string
     */
    protected $_tmpDir;

    /**
     * FileLock constructor.
     *
     * @param Filesystem $fs
     * @param string     $tmpDir
     */
    public function __construct(Filesystem $fs, $tmpDir)
    {
        $this->_fs = $fs;
        $this->_tmpDir = $tmpDir;
    }

    public function set($name, $timeout = 0)
    {
        $t = ($timeout == 0) ? 0 : (string)($this->__timestamp() + $timeout);

        $this->_fs->put($this->__path($name), $t);
    }

    public function remove($name)
    {
        $path = $this->__path($name);

        if ($this->_fs->has($path))
        {
            return $this->_fs->delete($path);
        }

        return true;
    }

    public function exists($name)
    {
        $file = $this->__path($name);

        if ($this->_fs->has($file))
        {
            if ($this->__timedOut($file))
            {
                $this->_fs->delete($file);

                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    private function __timestamp()
    {
        return round(microtime(true) * 1000);
    }

    private function __timedOut($lock)
    {
        $timeout = (double)$this->_fs->read($lock);
        
        return $timeout == 0 ? false : ($timeout - $this->__timestamp()) < 0;
    }

    private function __path($name)
    {
        return $this->_tmpDir . '/' . $name . '.lock';
    }
}