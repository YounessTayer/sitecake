<?php

namespace Sitecake\Services;

include_once 'Services_WebTestCase.php';

use Sitecake\Page;

/**
 * Class ContentServiceTest
 * @package Sitecake\Services
 */
class ContentServiceTest extends Services_WebTestCase
{
    public function setUp()
    {
        parent::setUp();

        // Edit session initialization
        $this->_site->startEdit();
    }

    public function testSaveWithoutPageID()
    {
        $client = $this->createClient();

        $client->request('POST', '/?service=_content&action=save', [
            'container' => 'dummy content'
        ]);

        $this->assertEquals($client->getResponse()->getStatusCode(), 400);
    }

    public function testSave()
    {
        $client = $this->createClient();

        // Get included un-name container name
        $page = new Page($this->_fs->read($this->_site->draftPath() . '/includes/unnamed-container.php'));
        $includedUnNamedContainerName = $page->containers()[0];

        // Get in-page un-named container name
        $page1 = new Page($this->_fs->read($this->_site->draftPath() . '/subdir/index.php'));
        $inPageUnNamedContainerName = $page1->containers()[1];

        $client->request('POST', '/?service=_content&action=save', [
            'scpageid' => '',
            'named' => base64_encode($this->_applyDraftPath(file_get_contents('../test-content/client/named.html'))),
            $includedUnNamedContainerName => base64_encode($this->_applyDraftPath(file_get_contents('../test-content/client/unnamed.html'))),
            $inPageUnNamedContainerName => base64_encode($this->_applyDraftPath(file_get_contents('../test-content/client/unnamed.html')))
        ]);

        // Assert response
        $this->assertValidJsonResponse($client);

        // Assert page content for named container
        $this->assertEquals(
            $this->_universalLineEndings(
                $this->_applyDraftPath(file_get_contents('../test-content/assert/draft/named.html'))
            ),
            $this->_universalLineEndings(
                $this->_fs->read($this->_site->draftPath() . '/includes/named-container.php')
            )
        );

        // Assert page content for included un-named container
        preg_match('/<div[^>]+>(.*)<\/div>/s', file_get_contents('../test-content/assert/draft/unnamed.html'), $expected);
        preg_match('/<div[^>]+>(.*)<\/div>/s', $this->_fs->read($this->_site->draftPath() . '/includes/unnamed-container.php'), $actual);
        $this->assertEquals(
            $this->_universalLineEndings($this->_applyDraftPath($expected[1])),
            $this->_universalLineEndings($actual[1])
        );

        // Assert page content for in-page un-named container
        preg_match('/<div[^>]+>(.*)<\/div>/s', file_get_contents('../test-content/assert/draft/unnamed.html'), $expected);
        preg_match('/<div class=\"sc\-content sc\-content\-_cnt_[0-9]+\">(.*)<\/div>/s', $this->_fs->read($this->_site->draftPath() . '/subdir/index.php'), $actual);
        $this->assertEquals(
            $this->_universalLineEndings($this->_applyDraftPath($expected[1])),
            $this->_universalLineEndings($actual[1])
        );

        // Assert draft dirty file exists
        $this->assertTrue($this->_fs->has($this->_site->draftPath() . '/draft.drt'));
    }

    public function testPublish()
    {
        $client = $this->createClient();

        $page = new Page($this->_fs->read($this->_site->draftPath() . '/includes/unnamed-container.php'));
        $unnanmed = $page->containers()[0];

        $client->request('POST', '/?service=_content&action=save', [
            'scpageid' => '',
            'named' => base64_encode($this->_applyDraftPath(file_get_contents('../test-content/client/named.html'))),
            $unnanmed => base64_encode($this->_applyDraftPath(file_get_contents('../test-content/client/unnamed.html')))
        ]);

        // Assert that original files are not changed yet
        $this->assertEquals(
            $this->_universalLineEndings(file_get_contents('../test-content/init/named.html')),
            $this->_universalLineEndings(
                $this->_fs->read('includes/named-container.php')
            )
        );
        $this->assertEquals(
            $this->_universalLineEndings(file_get_contents('../test-content/init/unnamed.html')),
            $this->_universalLineEndings(
                $this->_fs->read('includes/unnamed-container.php')
            )
        );
        $this->assertNotEquals(
            $this->_universalLineEndings(
                $this->_applyDraftPath($this->_fs->read('includes/named-container.php'))
            ),
            $this->_universalLineEndings(
                $this->_fs->read($this->_site->draftPath() . '/includes/named-container.php')
            )
        );
        $this->assertNotEquals(
            $this->_universalLineEndings(
                $this->_applyDraftPath($this->_fs->read('includes/unnamed-container.php'))
            ),
            $this->_universalLineEndings(
                $this->_fs->read($this->_site->draftPath() . '/includes/unnamed-container.php')
            )
        );

        // Get resource timestamps to be able to assert them after publishing
        $plainModified = $this->_fs->getMetadata('images/dummy-1540x866-commodore64-plain-sc65fe399a57b59.jpg')['timestamp'];
        $plain320Modified = $this->_fs->getMetadata('images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-320.jpg')['timestamp'];
        $plain640Modified = $this->_fs->getMetadata('images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-640.jpg')['timestamp'];
        $plain960Modified = $this->_fs->getMetadata('images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-960.jpg')['timestamp'];
        $plain1280Modified = $this->_fs->getMetadata('images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-1280.jpg')['timestamp'];

        // Create dummy backup dir to assert backup dir rotation
        $this->app['site.number_of_backups'] = 1;
        $this->_fs->write(
            $this->_site->backupPath() . '/' . date('Y-m-d-H.i.s') . '-' . substr(uniqid(), -2) . '/dummy', ''
        );

        // Make sure that timestamps will change
        sleep(1);

        $client->request('POST', '/?service=_content&action=publish', [
            'scpageid' => ''
        ]);

        $this->assertValidJsonResponse($client);

        // Assert published files
        $this->assertEquals(
            $this->_universalLineEndings(file_get_contents('../test-content/assert/published/named.html')),
            $this->_universalLineEndings($this->_fs->read('includes/named-container.php'))
        );
        preg_match('/<div[^>]+>(.*)<\/div>/s', file_get_contents('../test-content/assert/published/unnamed.html'), $expected);
        preg_match('/<div[^>]+>(.*)<\/div>/s', $this->_fs->read('includes/unnamed-container.php'), $actual);
        $this->assertEquals(
            $this->_universalLineEndings($expected[1]),
            $this->_universalLineEndings($actual[1])
        );

        // Assert that draft path was removed from resource urls
        $this->assertEquals(
            $this->_universalLineEndings(
                $this->_fs->read($this->_site->draftPath() . '/includes/named-container.php')
            ),
            $this->_universalLineEndings(
                $this->_applyDraftPath($this->_fs->read('includes/named-container.php'))
            )
        );
        preg_match('/<div[^>]+>(.*)<\/div>/s', $this->_fs->read($this->_site->draftPath() . '/includes/unnamed-container.php'), $expected);
        preg_match('/<div[^>]+>(.*)<\/div>/s', $this->_fs->read('includes/unnamed-container.php'), $actual);
        $this->assertEquals(
            $this->_universalLineEndings($expected[1]),
            $this->_universalLineEndings($this->_applyDraftPath($actual[1]))
        );

        // Assert resources modification time
        $plainModifiedAfterPublish = $this->_fs->getMetadata(
            'images/dummy-1540x866-commodore64-plain-sc65fe399a57b59.jpg'
        )['timestamp'];
        $plain320ModifiedAfterPublish = $this->_fs->getMetadata(
            'images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-320.jpg'
        )['timestamp'];
        $plain640ModifiedAfterPublish = $this->_fs->getMetadata(
            'images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-640.jpg'
        )['timestamp'];
        $plain960ModifiedAfterPublish = $this->_fs->getMetadata(
            'images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-960.jpg'
        )['timestamp'];
        $plain1280ModifiedAfterPublish = $this->_fs->getMetadata(
            'images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-1280.jpg'
        )['timestamp'];
        $this->assertTrue(($plainModified < $plainModifiedAfterPublish));
        $this->assertTrue(($plain320Modified < $plain320ModifiedAfterPublish));
        $this->assertTrue(($plain640Modified < $plain640ModifiedAfterPublish));
        $this->assertTrue(($plain960Modified < $plain960ModifiedAfterPublish));
        $this->assertTrue(($plain1280Modified < $plain1280ModifiedAfterPublish));

        // Assert backup creation
        $this->assertTrue($this->_fs->has($this->_site->backupPath()));
        $paths = $this->_fs->listPatternPaths(
            $this->_site->backupPath(), '/[0-9]{4}\-[0-9]{2}\-[0-9]{2}-[0-9]{2}.[0-9]{2}.[0-9]{2}\-[a-z0-9]{2}/'
        );
        $this->assertEquals(1, count($paths));
        $backupPath = $paths[0];
        $this->assertTrue($this->_fs->has($backupPath . '/files'));
        $this->assertTrue($this->_fs->has(
            $backupPath . '/images/dummy-1540x866-commodore64-plain-sc65fe399a57b59.jpg')
        );
        $this->assertTrue($this->_fs->has(
            $backupPath . '/images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-320.jpg')
        );
        $this->assertTrue($this->_fs->has(
            $backupPath . '/images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-640.jpg')
        );
        $this->assertTrue($this->_fs->has(
            $backupPath . '/images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-960.jpg')
        );
        $this->assertTrue($this->_fs->has(
            $backupPath . '/images/dummy-1540x866-commodore64-plain-sc56fe399a57b59-1280.jpg')
        );
        $this->assertTrue($this->_fs->has($backupPath . '/includes/named-container.php'));
        $this->assertTrue($this->_fs->has($backupPath . '/includes/unnamed-container.php'));
        $this->assertTrue($this->_fs->has($backupPath . '/subdir/index.php'));
        $this->assertTrue($this->_fs->has($backupPath . '/index.php'));
    }


}