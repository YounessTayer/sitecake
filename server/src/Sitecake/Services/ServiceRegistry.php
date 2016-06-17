<?php
/**
 * Created by PhpStorm.
 * User: PedaCarTicaShupak
 * Date: 1/23/2016
 * Time: 9:01 PM
 */

namespace Sitecake\Services;

use ReflectionClass;
use Silex\Application;

class ServiceRegistry
{
    protected static $_namespace = '\Sitecake\Services';

    /**
     * @var Application Contains references to loaded services
     */
    protected static $_context;

    public static function initialize($context = [])
    {
        self::$_context = $context;
    }

    public static function get($name)
    {
        if(isset(self::$_context[$name]))
        {
            return self::$_context[$name];
        }

        $normalized = self::normalizeServiceName($name);
        $reflection = new ReflectionClass(
            self::$_namespace . '\\' . $normalized . '\\' . $normalized . 'Service'
        );

        self::$_context[$name] = self::$_context->share(function($ctx) use ($reflection) {
            return $reflection->newInstance(self::$_context);
        });

        return self::$_context[$name];
    }

    public static function normalizeServiceName($name)
    {
        return ucfirst(substr($name, 1));
    }
}