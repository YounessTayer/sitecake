<?php

namespace Sitecake\Services;

include_once 'Services_WebTestCase.php';

use Sitecake\Error\ErrorHandler;
use Sitecake\SessionManager;

class SessionServiceTest extends Services_WebTestCase
{
    const DEFAULT_PASSWORD = 'd033e22ae348aeb5660fc2140aec35850c4da997';

    public function setUp()
    {
        parent::setUp();

        // Remove login lock file and kill session
        $this->app['session']->invalidate(0);
        $this->app['flock']->remove('login');
    }

    public function tearDown()
    {
        // Update user credentials to default after each test method
        $this->_fs->put($this->app['CREDENTIALS_PATH'],
            '<?php $credentials = "' . self::DEFAULT_PASSWORD . '";');

        parent::tearDown();
    }

    public function testLoginValid()
    {
        $client = $this->createClient();
        $client->request('GET', '/?service=_session&action=login&credentials=' . self::DEFAULT_PASSWORD);

        $this->assertValidJsonResponse($client);

        $this->assertJsonStringEqualsJsonString(
            json_encode(["status" => 0]), $client->getResponse()->getContent()
        );
        $this->assertTrue($this->app['session']->get('loggedin'));
        $this->assertTrue($this->app['flock']->exists('login'));
    }

    public function testLoginFromAnotherUser()
    {
        $client = $this->createClient();

        // Simulate login from another user
        $this->app['flock']->set('login', SessionManager::SESSION_TIMEOUT);

        $client->request('GET', '/?service=_session&action=login&credentials=' . self::DEFAULT_PASSWORD);

        $this->assertValidJsonResponse($client);

        $this->assertJsonStringEqualsJsonString(
            json_encode(array("status" => 2)), $client->getResponse()->getContent()
        );
        $this->assertFalse($this->app['session']->has('loggedin'));
        $this->assertTrue($this->app['flock']->exists('login'));
    }

    public function testLoginWithWrongPassword()
    {
        $client = $this->createClient();

        $client->request('GET', '/?service=_session&action=login&credentials=wrong_password');

        $this->assertValidJsonResponse($client);

        $this->assertJsonStringEqualsJsonString(
            json_encode(array("status" => 1)), $client->getResponse()->getContent()
        );
        $this->assertFalse($this->app['session']->has('loggedin'));
        $this->assertFalse($this->app['flock']->exists('login'));
    }

    public function testChangeNoDataPassed()
    {
        $client = $this->createClient();

        $client->request('GET', '/?service=_session&action=change');

        $this->assertEquals($client->getResponse()->getStatusCode(), 400);

        /** @var string $credentials */
        require $this->_fs->getAdapter()->applyPathPrefix($this->app['CREDENTIALS_PATH']);

        $this->assertEquals(self::DEFAULT_PASSWORD, $credentials);
    }

    public function testChangeWrongCurrentPassword()
    {
        $client = $this->createClient();

        $client->request('GET', '/?service=_session&action=change' .
                                '&credentials=wrong_password' .
                                '&newCredentials=new_credentials');

        $this->assertValidJsonResponse($client);

        $this->assertJsonStringEqualsJsonString(
            json_encode(array("status" => 1)), $client->getResponse()->getContent()
        );

        /** @var string $credentials */
        require $this->_fs->getAdapter()->applyPathPrefix($this->app['CREDENTIALS_PATH']);

        $this->assertNotEquals('new_credentials', $credentials);
    }

    public function testChangeValid()
    {
        $client = $this->createClient();

        $client->request('GET', '/?service=_session&action=change' .
                                '&credentials=' . self::DEFAULT_PASSWORD .
                                '&newCredentials=new_credentials');

        $this->assertValidJsonResponse($client);

        $this->assertJsonStringEqualsJsonString(
            json_encode(array("status" => 0)), $client->getResponse()->getContent()
        );

        /** @var string $credentials */
        require $this->_fs->getAdapter()->applyPathPrefix($this->app['CREDENTIALS_PATH']);

        $this->assertEquals('new_credentials', $credentials);
    }

    public function testLogout()
    {
        $client = $this->createClient();

        // Simulate Login
        $this->app['session']->set('loggedin', true);
        $this->app['flock']->set('login', SessionManager::SESSION_TIMEOUT);

        $client->request('GET', '/?service=_session&action=logout');

        $this->assertValidJsonResponse($client);

        $this->assertJsonStringEqualsJsonString(
            json_encode(array("status" => 0)), $client->getResponse()->getContent()
        );
        $this->assertFalse($this->app['session']->has('loggedin'));
        $this->assertFalse($this->app['flock']->exists('login'));
    }

    public function checkAlive()
    {
        $client = $this->createClient();

        // Simulate Login
        $this->app['session']->set('loggedin', true);
        $this->app['flock']->set('login', SessionManager::SESSION_TIMEOUT);

        $timestamp = $this->_fs->read($this->_site->tmpPath() . '/login.lock');

        $client->request('GET', '/?service=_session&action=alive');


        $updatedTimestamp = $this->_fs->readsite($this->_site->tmpPath() . '/login.lock');

        $this->assertGreaterThan($timestamp, $updatedTimestamp);

    }

    public function testIsAuthRequired()
    {
        $client = $this->createClient();

        ErrorHandler::suppress(ErrorHandler::SUPPRESS_ALL);

        $client->request('GET', '/?service=_session&action=logout');

        $this->assertEquals($client->getResponse()->getStatusCode(), 401);


        $client->request('GET', '/?service=_session&action=alive');

        $this->assertEquals($client->getResponse()->getStatusCode(), 401);
    }
}