<?php

if (defined('SITECAKE_ENVIRONMENT') && in_array(SITECAKE_ENVIRONMENT, ['prod', 'dev']))
{
    require __DIR__ . '/check.php';
}

require __DIR__ . '/../vendor/autoload.php';

use JDesrosiers\Silex\Provider\CorsServiceProvider;
use League\Flysystem\Filesystem;
use League\Flysystem\Adapter\Local as AdapterLocal;
use League\Flysystem\Adapter\Ftp as AdapterFtp;
use Sitecake\Error\ErrorHandler;

// instantiate Silex application
$app = new Silex\Application();

// Detect environment (default: prod) by checking for the existence of SITECAKE_ENVIRONMENT constant
if (defined('SITECAKE_ENVIRONMENT') && in_array(SITECAKE_ENVIRONMENT, ['prod', 'dev', 'test']))
{
    $app['environment'] = SITECAKE_ENVIRONMENT;
}
else
{
    $app['environment'] = 'prod';
}

// An absolute filesystem path to the site root directory (where sitecake.php is located too).
// It is used only to instantiate the filesystem abstraction. From this point on, all
// paths are relative (to the BASE_DIR) and all paths can be used as relative URLs as well.
$app['BASE_DIR'] = realpath(__DIR__ . '/../../../');

// URL relative to sitecake.php that Sitecake editor is using as the entry point
// to the CMS service API
$app['SERVICE_URL'] = 'sitecake/${version}/src/app.php';

// URL relative to sitecake.php that Sitecake editor is using to load the login module
$app['EDITOR_LOGIN_URL'] = 'sitecake/${version}/client/publicmanager/publicmanager.nocache.js';

// URL relative to sitecake.php that Sitecake editor is using to load the editor module
$app['EDITOR_EDIT_URL'] = 'sitecake/${version}/client/contentmanager/contentmanager.nocache.js';

// URL relative to sitecake.php that Sitecake editor is using to load the editor configuration
$app['EDITOR_CONFIG_URL'] = 'sitecake/editor.cnf';

// A relative path to sitecake credential file
$app['CREDENTIALS_PATH'] = 'sitecake/credentials.php';

// Include the server-side configuration that user is expected to modify
require __DIR__ . '/config.php';

// Include test bootstrap
if ($app['environment'] == 'test')
{
    require __DIR__ . '/../tests/bootstrap.php';
}

// Set default timezone
date_default_timezone_set('UTC');

// Register error reporting
if(!$app['debug'])
{
    $app['exception_handler']->disable();
    ErrorHandler::register($app);
}

// Configure the abstract file system
if ($app['filesystem.adapter'] == 'local')
{
    $app['fs'] = $app->share(function ($app)
    {
        return new Filesystem(new AdapterLocal($app['BASE_DIR']));
    });
}
else if ($app['filesystem.adapter'] == 'ftp')
{
    $app['fs'] = $app->share(function ($app)
    {
        return new Filesystem(new AdapterFtp($app['filesystem.adapter.config']));
    });
}
else
{
    trigger_error(
        sprintf('Unsupported filesystem.adapter %s. Supported types are local and ftp. Please check the configuration.',
            $app['filesystem.adapter'])
    );
}

// Add application specific filesystem plugins
$app['fs']->addPlugin(new Sitecake\Filesystem\EnsureDirectory);
$app['fs']->addPlugin(new Sitecake\Filesystem\ListPatternPaths);
$app['fs']->addPlugin(new Sitecake\Filesystem\RandomDirectory);
$app['fs']->addPlugin(new Sitecake\Filesystem\CopyPaths);
$app['fs']->addPlugin(new Sitecake\Filesystem\DeletePaths);

// Set logger
if(!isset($app['log']) || $app['log'] !== false)
{
    $app['logger'] = $app->share(function ($app)
    {
        return new Sitecake\Log\Engine\FileLog($app['fs'], $app);
    });
    Sitecake\Log\Log::init($app['logger']);
}

// Register session provider
$app->register(new Sitecake\SessionServiceProvider());

// Register Translation provider
$app->register(new Silex\Provider\TranslationServiceProvider(), [
    'locale_fallbacks' => ['en'],
]);

// Register cross-origin resource sharing (CORS) provider
$app->register(new CorsServiceProvider(), []);
$app->after($app["cors"]);

// Set translator
$app['translator'] = $app->share($app->extend('translator', function ($translator, $app)
{
    $translator->addLoader('yaml', new Symfony\Component\Translation\Loader\YamlFileLoader());
    $translator->addResource('yaml', __DIR__ . '/locale/en.yml', 'en');

    return $translator;
}));

// Set Auth
$app['auth'] = $app->share(function ($app)
{
    return new Sitecake\Auth\Auth($app['fs'], $app['CREDENTIALS_PATH']);
});

$app['site'] = $app->share(function ($app)
{
    return new Sitecake\Site($app['fs'], $app);
});

$app['flock'] = $app->share(function ($app)
{
    return new Sitecake\FileLock($app['fs'], $app['site']->tmpPath());
});

$app['sm'] = $app->share(function ($app)
{
    return new Sitecake\SessionManager($app['session'], $app['flock'], $app['auth'], $app['site']);
});

$app['renderer'] = $app->share(function ($app)
{
    return new Sitecake\Renderer($app['site'], $app);
});
