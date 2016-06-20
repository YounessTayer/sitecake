<?php

namespace Sitecake\Log\Engine;

use League\Flysystem\FilesystemInterface;
use Sitecake\Util\Utils;

class FileLog extends BaseLog
{
    /**
     * Number of log archives to be kept on server
     * @var int
     */
    protected $_archiveSize = 0;

    /**
     * Log files directory relative path
     * @var string
     */
    protected $_path = 'sitecake-temp/logs';

    /**
     * Filename for application errors
     * @var string
     */
    protected $_file = 'sitecake.log';

    /**
     * Maximum size of one log file before it is archived
     * @var mixed
     */
    protected $_size;

    /**
     * @var FilesystemInterface
     */
    protected $_fs;

    /**
     * Debug log types
     * @var array
     */
    protected $_debugTypes = ['notice', 'info', 'debug'];

    /**
     * Specifies current debug mode
     * @var bool
     */
    protected $_debugMode = false;

    /**
     * FileLog constructor.
     *
     * @param FilesystemInterface $fs
     * @param array               $config
     */
    public function __construct(FilesystemInterface $fs, $config = [])
    {
        $this->_fs = $fs;

        // Read debug mode from app configuration
        if (isset($config['debug']) && !empty($config['debug']))
        {
            $this->_debugMode = true;
        }

        // Read log file size before file is archived
        if (isset($config['log.size']))
        {
            if (is_numeric($config['log.size']))
            {
                $this->_size = (int)$config['log.size'];
            }
            else
            {
                $this->_size = Utils::parseFileSize($config['log.size']);
            }
        }

        // Read number of log archive files kept
        if (isset($config['log.archive_size']))
        {
            $this->_archiveSize = $config['log.archive_size'];
        }

        // Ensure log directory
        try
        {
            if (isset($config['log.path']))
            {
                $pathParts = explode('/', $config['log.path']);
                $this->_file = array_pop($pathParts);
                $this->_path = implode('/', $pathParts);
            }
            if (!$this->_fs->ensureDir($this->_path))
            {
                throw new \LogicException(
                    sprintf('Could not ensure that the directory %s is present and writable.', $this->_path)
                );
            }
        }
        catch (\RuntimeException $e)
        {
            throw new \LogicException('Could not ensure that the directory /sitecake/logs is present and writable.');
        }
    }

    /**
     * Implements writing to log files.
     *
     * @param string $level   The severity level of the message being written.
     *                        See Cake\Log\Log::$_levels for list of possible levels.
     * @param string $message The message you want to log.
     * @param array  $context Additional information about the logged message
     *
     * @return bool success of write.
     */
    public function log($level, $message, array $context = [])
    {
        if (in_array($level, $this->_debugTypes) && !$this->_debugMode)
        {
            return true;
        }
        $message = $this->_format($message, $context);

        $output = '[' . date('Y-m-d H:i:s') . ']' . ' ' . ucfirst($level) . ': ' . $message . "\n";

        $filename = $this->_getFilename($level);

        $pathname = $this->_path . '/' . $filename;

        // Ensure log files
        if(!$this->_fs->has($pathname))
        {
            $this->_fs->write($pathname, '');
        }

        if (!empty($this->_size))
        {
            $this->_archiveFile($filename);
        }

        $content = $this->_fs->read($pathname);
        return (bool)$this->_fs->put($pathname, $content . $output);
    }

    /**
     * Get filename based on log level
     *
     * @param string $level The level of log.
     *
     * @return string File name
     */
    protected function _getFilename($level)
    {
        $filename = $this->_file;

        if (in_array($level, $this->_debugTypes))
        {
            $filename = 'sc-debug.log';
        }

        return $filename;
    }

    /**
     * Archive log file if size specified in config is reached.
     * Also if `rotate` count is reached oldest file is removed.
     *
     * @param string $filename Log file name
     *
     * @return bool True if archived successfully or no need for archiving or false in case of error.
     */
    protected function _archiveFile($filename)
    {
        $filePath = $this->_path . '/' . $filename;
        clearstatcache(true, $filePath);

        $metadata = $this->_fs->getMetadata($filePath);

        if ($metadata['size'] < $this->_size)
        {
            return true;
        }

        if ($this->_archiveSize === 0)
        {
            $result = $this->_fs->delete($filePath);
        }
        else
        {
            $result = $this->_fs->rename($filePath, $filePath . '.' . time());
            $this->_fs->write($filePath, '');
        }

        $files = glob($filePath . '.*');
        if ($files)
        {
            $filesToDelete = count($files) - $this->_archiveSize;
            while ($filesToDelete > 0)
            {
                $this->_fs->delete(array_shift($files));
                $filesToDelete--;
            }
        }

        return $result;
    }
}