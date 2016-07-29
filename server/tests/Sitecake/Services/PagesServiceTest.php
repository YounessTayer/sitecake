<?php

namespace Sitecake\Services;

include_once 'Services_WebTestCase.php';

use \phpQuery;
use Sitecake\Menu;

class PagesServiceTest extends Services_WebTestCase
{
    public function testPages()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('GET', '/?service=_pages&action=pages');

        $this->assertValidJsonResponse($client);

        $response = json_decode($client->getResponse()->getContent(), true);

        $this->assertArrayHasKey('pages', $response);
        $this->assertNotEmpty($response['pages']);

        $pages = $response['pages'];

        $this->assertEquals(2, count($pages));

        $this->assertNotEmpty($pages[0]);
        $this->assertArrayHasKey('id', $pages[0]);
        $this->assertNotEmpty($pages[0]['id']);
        $this->assertEquals('index.php', $pages[0]['url']);
        $this->assertEquals('Index Test', $pages[0]['title']);
        $this->assertEquals('Index Description', $pages[0]['desc']);
        $this->assertEquals(-1, $pages[0]['idx']);
        $this->assertEmpty($pages[0]['navtitle']);

        $this->assertNotEmpty($pages[1]);
        $this->assertArrayHasKey('id', $pages[1]);
        $this->assertNotEmpty($pages[1]['id']);
        $this->assertEquals('subdir/index.php', $pages[1]['url']);
        $this->assertEquals('Subdir Test', $pages[1]['title']);
        $this->assertEquals('Subdir Description', $pages[1]['desc']);
        $this->assertEquals(0, $pages[1]['idx']);
        $this->assertEquals('Subdir Page', $pages[1]['navtitle']);
    }

    public function testSave()
    {
        // Use page with content to be able to assert resource duplication
        $this->_fs->put('subdir/index.php', file_get_contents('../test-content/pages/subdir-index-with-content.php'));
        // Copy page that should be deleted after save
        $this->_fs->put('for-deletion.html', file_get_contents('../test-content/pages/for-deletion.html'));

        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        // Load current metadata to
        $metadata = $this->_site->loadMetadata();
        $this->assertArrayHasKey('pages', $metadata);
        $this->assertNotEmpty($metadata['pages']);
        $pages = $metadata['pages'];
        $indexID = $pages['index.php']['id'];
        $subDirID = $pages['subdir/index.php']['id'];

        $client->request('POST', '/?service=_pages&action=pages', [
            'pages' => json_encode([
                // new page from index.php
                [
                    'tid' => $subDirID,
                    'url' => 'subdir/new-page.php',
                    'title' => 'New Test',
                    'desc' => 'New Description',
                    'idx' => 1,
                    'navtitle' => 'New Page',
                ],
                // index.php
                [
                    'id' => $indexID,
                    'url' => 'index.php',
                    'title' => 'Index Test altered',
                    'desc' => 'Index Description altered',
                    'idx' => 0,
                    'navtitle' => 'Index Page',
                ],
                // subdir/index.php
                [
                    'id' => $subDirID,
                    'url' => 'subdir/index.php',
                    'title' => 'Subdir Test altered',
                    'desc' => 'Subdir Description altered',
                    'idx' => -1,
                    'navtitle' => '',
                ]
            ])
        ]);

        $this->assertValidJsonResponse($client);

        // Assert page existence
        $this->assertTrue($this->_fs->has('index.php'));
        $this->assertTrue($this->_fs->has('subdir/new-page.php'));
        $this->assertTrue($this->_fs->has('subdir/index.php'));
        $this->assertFalse($this->_fs->has('for-deletion.html'));

        // Assert metadata consistency
        $metadata = $this->_site->loadMetadata();
        $this->assertArrayHasKey('pages', $metadata);
        $this->assertNotEmpty($metadata['pages']);

        $pages = $metadata['pages'];
        $this->assertNotEmpty($pages['subdir/new-page.php']);
        $this->assertArrayHasKey('id', $pages['subdir/new-page.php']);
        $this->assertNotEmpty($pages['subdir/new-page.php']['id']);
        $this->assertEquals('subdir/new-page.php', $pages['subdir/new-page.php']['url']);
        $this->assertEquals('New Test', $pages['subdir/new-page.php']['title']);
        $this->assertEquals('New Description', $pages['subdir/new-page.php']['desc']);
        $this->assertEquals(1, $pages['subdir/new-page.php']['idx']);
        $this->assertEquals('New Page', $pages['subdir/new-page.php']['navtitle']);

        $this->assertNotEmpty($pages['index.php']);
        $this->assertArrayHasKey('id', $pages['index.php']);
        $this->assertEquals($indexID, $pages['index.php']['id']);
        $this->assertEquals('index.php', $pages['index.php']['url']);
        $this->assertEquals('Index Test altered', $pages['index.php']['title']);
        $this->assertEquals('Index Description altered', $pages['index.php']['desc']);
        $this->assertEquals(0, $pages['index.php']['idx']);
        $this->assertEquals('Index Page', $pages['index.php']['navtitle']);

        $this->assertNotEmpty($pages['subdir/index.php']);
        $this->assertArrayHasKey('id', $pages['subdir/index.php']);
        $this->assertEquals($subDirID, $pages['subdir/index.php']['id']);
        $this->assertEquals('subdir/index.php', $pages['subdir/index.php']['url']);
        $this->assertEquals('Subdir Test altered', $pages['subdir/index.php']['title']);
        $this->assertEquals('Subdir Description altered', $pages['subdir/index.php']['desc']);
        $this->assertEquals(-1, $pages['subdir/index.php']['idx']);
        $this->assertEmpty($pages['subdir/index.php']['navtitle']);

        // Assert menu generation
        foreach($pages as $page => $details)
        {
            phpQuery::newDocument($this->_fs->read($page));
            $this->assertEquals(1, phpQuery::pq('.' . Menu::SC_MENU_BASE_CLASS)->count());

            $menuItems = phpQuery::pq('.' . Menu::SC_MENU_BASE_CLASS . ' a');
            $this->assertEquals(2, $menuItems->count());

            $urls = [];
            foreach($menuItems as $menuItem)
            {
                $urls[] = $menuItem->getAttribute('href');
            }
            $this->assertTrue(in_array('index.php', $urls));
            $this->assertTrue(in_array('subdir/new-page.php', $urls));
            $this->assertFalse(in_array('subdir/index.php', $urls));
        }

        // Assert resources duplication for unnamed container
        $this->assertEquals(2, count($this->_fs->listPatternPaths(
            'images', '/dummy\-1540x866\-commodore64\-plain\-sc[0-9a-f]{13}\-320\.jpg/'
        )));
        $this->assertEquals(2, count($this->_fs->listPatternPaths(
            'images', '/dummy\-1540x866\-commodore64\-plain\-sc[0-9a-f]{13}\-640\.jpg/'
        )));
        $this->assertEquals(2, count($this->_fs->listPatternPaths(
            'images', '/dummy\-1540x866\-commodore64\-plain\-sc[0-9a-f]{13}\-960\.jpg/'
        )));
        $this->assertEquals(2, count($this->_fs->listPatternPaths(
            'images', '/dummy\-1540x866\-commodore64\-plain\-sc[0-9a-f]{13}\-1280\.jpg/'
        )));
        $this->assertEquals(1, count($this->_fs->listPatternPaths(
            'images', '/dummy\-1540x866\-commodore64\-plain\-sc[0-9a-f]{13}\.jpg/'
        )));
    }
}