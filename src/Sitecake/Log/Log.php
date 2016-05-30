<?php

namespace Sitecake\Log;

use Psr\Log\InvalidArgumentException;
use Psr\Log\LoggerInterface;

class Log
{
    /**
     * Log engine
     * @var LoggerInterface
     */
    protected static $_logger;

    /**
     * Handled log levels
     *
     * @var array
     */
    protected static $_levels = [
        'emergency',
        'alert',
        'critical',
        'error',
        'warning',
        'notice',
        'info',
        'debug'
    ];

    /**
     * Log levels as detailed in RFC 5424
     *
     * @var array
     */
    protected static $_levelMap = [
        'emergency' => LOG_EMERG,
        'alert' => LOG_ALERT,
        'critical' => LOG_CRIT,
        'error' => LOG_ERR,
        'warning' => LOG_WARNING,
        'notice' => LOG_NOTICE,
        'info' => LOG_INFO,
        'debug' => LOG_DEBUG,
    ];

    public static function init(LoggerInterface $logger)
    {
        static::$_logger = $logger;
    }

    public static function write($level, $message, $context = [])
    {
        if(!static::$_logger)
        {
            return;
        }

        if (is_numeric($level) && in_array($level, static::$_levelMap)) {
            $level = array_search($level, static::$_levelMap);
        }


        if (!in_array($level, static::$_levels)) {
            throw new InvalidArgumentException(sprintf('Invalid log level "%s"', $level));
        }

        if(!method_exists(static::$_logger, 'log'))
        {
            throw new \LogicException('Logger have to implement method \'log\'');
        }

        static::$_logger->log($level, $message, $context);

        return true;
    }
}