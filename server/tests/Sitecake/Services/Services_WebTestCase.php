<?php

namespace Sitecake\Services;

use League\Flysystem\Filesystem;
use Silex\WebTestCase;
use Sitecake\SessionManager;
use Sitecake\Site;
use Symfony\Component\HttpKernel\Client;

abstract class Services_WebTestCase extends WebTestCase
{
    private $__execDir;

    /**
     * @var Site
     */
    protected $_site;

    /**
     * @var Filesystem
     */
    protected $_fs;

    /**
     * Set up test environment
     */
    public function setUp()
    {
        // Create test-site dir if it doesn't exist
        if(!is_dir(__DIR__ . '/../../test-site'))
        {
            mkdir(__DIR__ . '/../../test-site', 0777);
        }

        parent::setUp();

        $this->_site = $this->app['site'];
        
        $this->_fs = $this->app['fs'];

        // Remember executing directory
        $this->__execDir = getcwd();
        chdir($this->app['BASE_DIR']);

        // Simulate login
        $this->app['session']->set('loggedin', true);
        $this->app['flock']->set('login', SessionManager::SESSION_TIMEOUT);

        // Copy initial pages to test-site dir
        $this->_fs->put('.scignore', file_get_contents('../test-content/init/.scignore'));
        $this->_fs->put('.scpages', file_get_contents('../test-content/init/.scpages'));
        $this->_fs->put('credentials.php', file_get_contents('../test-content/init/credentials.php'));
        $this->_fs->put('index.php', file_get_contents('../test-content/pages/index.php'));
        $this->_fs->put('subdir/index.php', file_get_contents('../test-content/pages/subdir-index.php'));
        // Copy initial includes with content to test-site dir
        $this->_fs->put('includes/named-container.php', file_get_contents('../test-content/init/named.html'));
        $this->_fs->put('includes/unnamed-container.php', file_get_contents('../test-content/init/unnamed.html'));
        // Copy resources to test-site dir
        $this->_fs->put(
            'images/dummy-1540x866-commodore64-plain-sc65fe399a57b59.jpg',
            file_get_contents('../test-content/images/dummy-1540x866-commodore64-plain.jpg')
        );
        $this->_fs->put(
            'images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-320.jpg',
            file_get_contents('../test-content/images/dummy-1540x866-commodore64-plain-320.jpg')
        );
        $this->_fs->put(
            'images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-640.jpg',
            file_get_contents('../test-content/images/dummy-1540x866-commodore64-plain-640.jpg')
        );
        $this->_fs->put(
            'images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-960.jpg',
            file_get_contents('../test-content/images/dummy-1540x866-commodore64-plain-960.jpg')
        );
        $this->_fs->put(
            'images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-1280.jpg',
            file_get_contents('../test-content/images/dummy-1540x866-commodore64-plain-1280.jpg')
        );
    }

    protected function tearDown()
    {
        if($this->_fs->has('index.php'))
        {
            $this->_fs->delete('index.php');
        }
        if($this->_fs->has('includes'))
        {
            $this->_fs->deleteDir('includes');
        }
        if($this->_fs->has('images'))
        {
            $this->_fs->deleteDir('images');
        }
        if($this->_fs->has('files'))
        {
            $this->_fs->deleteDir('files');
        }
        if($this->_fs->has('subdir'))
        {
            $this->_fs->deleteDir('subdir');
        }
        if($this->_fs->has('sitecake-backup'))
        {
            $this->_fs->deleteDir('sitecake-backup');
        }
        if($this->_fs->has('sitecake-temp'))
        {
            $this->_fs->deleteDir('sitecake-temp');
        }
        if($this->_fs->has('sitecake-temp'))
        {
            $this->_fs->deleteDir('sitecake-temp');
        }
        chdir($this->__execDir);
    }

    public function createApplication()
    {
        if(!defined('SITECAKE_ENVIRONMENT'))
        {
            define('SITECAKE_ENVIRONMENT', 'test');
        }
        $app = require __DIR__.'/../../../src/app.php';
        $app['debug'] = true;
        $app['session.test'] = true;
        $app['exception_handler']->disable();

        return $app;
    }

    public function assertValidJsonResponse(Client $client)
    {
        $this->assertTrue($client->getResponse()->isOk());
        $this->assertTrue(
            $client->getResponse()->headers->contains(
                'Content-Type',
                'application/json'
            )
        );
    }

    protected function _universalLineEndings($content)
    {
        return preg_replace('~\R~u', "\r\n", $content);
    }

    protected function _applyDraftPath($content)
    {
        $content = str_replace('images/', $this->_site->draftPath() . '/images/', $content);
        $content = str_replace('files/', $this->_site->draftPath() . '/files/', $content);

        return $content;
    }
}