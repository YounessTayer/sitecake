<?php

namespace Sitecake;

include_once 'Services/Services_WebTestCase.php';

use Sitecake\Error\ErrorHandler;
use Sitecake\Services\Services_WebTestCase;

class RendererTest extends Services_WebTestCase
{
    public function createApplication()
	{
		if(!defined('SITECAKE_ENVIRONMENT'))
		{
			define('SITECAKE_ENVIRONMENT', 'test');
		}
		$app = require __DIR__.'/../../src/app.php';
		$app['debug'] = true;
		$app['session.test'] = true;
		$app['exception_handler']->disable();

		return $app;
	}

	public function testLoginResponse()
	{
        // Remove login lock file and kill session
        $this->app['session']->invalidate(0);
        $this->app['flock']->remove('login');

		$client = $this->createClient();

		$crawler = $client->request('GET', '/');

        $this->assertEquals(2, $crawler->filter('head script')->count());

        $scVariables = $crawler->filterXpath('//head/script[1]')->text();
        $this->assertRegExp('/var sitecakeGlobals/', $scVariables);
        $this->assertRegExp('/editMode\s?:\s?false,/', $scVariables);
        $this->assertRegExp('/serverVersionId\s?:\s?.+,/', $scVariables);
        $this->assertRegExp('/phpVersion\s?:\s?\"[0-9.]+.*@.+\",/', $scVariables);
        $this->assertRegExp('/serviceUrl\s?:\s?\"' . preg_quote($this->app['SERVICE_URL'], '/') . '\",/',
            $scVariables);
        $this->assertRegExp('/configUrl\s?:\s?\"' . preg_quote($this->app['EDITOR_CONFIG_URL'], '/') . '\",/',
            $scVariables);
        $this->assertRegExp('/forceLoginDialog\s?:\s?true/', $scVariables);

        $this->assertEquals($this->app['EDITOR_LOGIN_URL'],
            urldecode($crawler->filterXpath('//head/script[2]')->attr('src')));
	}

    public function testEditResponse()
    {
        $client = $this->createClient();

        // Do request
        $crawler = $client->request('GET', '/?page=index.php');

        /**
         * Assert page content
         */
        $this->assertEquals(2, $crawler->filter('head script')->count());

        // Assert sc variables presence
        $scVariables = $crawler->filterXpath('//head/script[1]')->text();
        $this->assertRegExp('/var sitecakeGlobals/', $scVariables);
        $this->assertRegExp('/editMode\s?:\s?true,/', $scVariables);
        $this->assertRegExp('/serverVersionId\s?:\s?.+,/', $scVariables);
        $this->assertRegExp('/phpVersion\s?:\s?\"[0-9.]+.*@.+\",/', $scVariables);
        $this->assertRegExp('/serviceUrl\s?:\s?\"' . preg_quote($this->app['SERVICE_URL'], '/') . '\",/',
            $scVariables);
        $this->assertRegExp('/configUrl\s?:\s?\"' . preg_quote($this->app['EDITOR_CONFIG_URL'], '/') . '\",/',
            $scVariables);
        $this->assertRegExp('/draftPublished\s?:\s?true/', $scVariables);

        // Assert editor inclusion
        $this->assertEquals($this->app['EDITOR_EDIT_URL'],
            urldecode($crawler->filterXpath('//head/script[2]')->attr('src')));

        // Assert container presence
        $this->assertEquals('sc-content-named', $crawler->filterXpath('//body/div[1]')->attr('class'));
        $this->assertRegExp(
            '/(^|\s)(sc\-content\-_cnt_[0-9]+)($|\s)/',
            $crawler->filterXpath('//body/div[2]')->attr('class')
        );

        // Assert draft pages creation
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() . '/draft.mkr'));
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() . '/index.php'));
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() . '/subdir/index.php'));
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() .
                                                '/includes/named-container.php'));
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() .
                                                '/includes/unnamed-container.php'));
        $this->assertFalse($this->app['fs']->has($this->app['site']->draftPath() .
                                                 '/credentials.php'));


        // Assert draft resources creation
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() .
                                                '/images/dummy-1540x866-commodore64-plain-sc65fe399a57b59.jpg'));
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() .
                                                '/images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-320.jpg'));
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() .
                                                '/images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-640.jpg'));
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() .
                                                '/images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-960.jpg'));
        $this->assertTrue($this->app['fs']->has($this->app['site']->draftPath() .
                                                '/images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-1280.jpg'));
    }

    public function testEditResponseDefaultIndex()
    {
        $client = $this->createClient();

        // Do request
        $crawler = $client->request('GET', '/');

        $this->assertEquals(2, $crawler->filter('head script')->count());

        $scVariables = $crawler->filterXpath('//head/script[1]')->text();
        $this->assertRegExp('/var sitecakeGlobals/', $scVariables);
        $this->assertRegExp('/editMode\s?:\s?true,/', $scVariables);
        $this->assertRegExp('/serverVersionId\s?:\s?.+,/', $scVariables);
        $this->assertRegExp('/phpVersion\s?:\s?\"[0-9.]+.*@.+\",/', $scVariables);
        $this->assertRegExp('/serviceUrl\s?:\s?\"' . preg_quote($this->app['SERVICE_URL'], '/') . '\",/',
            $scVariables);
        $this->assertRegExp('/configUrl\s?:\s?\"' . preg_quote($this->app['EDITOR_CONFIG_URL'], '/') . '\",/',
            $scVariables);
        $this->assertRegExp('/draftPublished\s?:\s?true/', $scVariables);

        $this->assertEquals($this->app['EDITOR_EDIT_URL'],
            urldecode($crawler->filterXpath('//head/script[2]')->attr('src')));
    }

    public function testEditResponsePageNotFound()
    {
        $client = $this->createClient();

        ErrorHandler::suppress();

        // Do request
        $client->request('GET', '/?page=nonexistingpage.php');

        $this->assertEquals($client->getResponse()->getStatusCode(), 401);
    }
}