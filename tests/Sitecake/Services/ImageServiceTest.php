<?php

namespace Sitecake\Services;

include_once 'Services_WebTestCase.php';

use \phpQuery;

class ImageServiceTest extends Services_WebTestCase
{
    protected $_externalSource = 'http://www.42do.net/sitecake/logo.png';

    public function testUploadNoXFilenameHeader()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_image&action=upload');

        $this->assertEquals($client->getResponse()->getStatusCode(), 400);
    }

    public function testUpload()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        // Update WideImage class map within composer
        $classLoader = require __DIR__ . '/../../../vendor/autoload.php';
        $classLoader->addClassMap(['WideImage\WideImage' => dirname(dirname(__FILE__)) . '/Mock/WideImage.php']);

        $client->request('GET', '/?service=_image&action=upload', [], [], [
            'HTTP_X-FILENAME' => base64_encode('test-image.jpg')
        ]);

        $this->assertValidJsonResponse($client);

        $response = json_decode($client->getResponse()->getContent(), true);

        $this->assertCount(3, $response);

        // Assert response status
        $this->assertArrayHasKey('status', $response);
        $this->assertEquals(0, $response['status']);

        // Assert response srcset
        $this->assertArrayHasKey('srcset', $response);
        $this->assertTrue(is_array($response['srcset']));
        $this->assertCount(4, $response['srcset']);
        $this->assertEquals(1280, $response['srcset'][0]['width']);
        $this->assertEquals(720, $response['srcset'][0]['height']);
        $this->assertEquals(0, strpos($response['srcset'][0]['url'], $this->_site->draftPath()));
        $this->assertEquals(960, $response['srcset'][1]['width']);
        $this->assertEquals(540, $response['srcset'][1]['height']);
        $this->assertEquals(0, strpos($response['srcset'][1]['url'], $this->_site->draftPath()));
        $this->assertEquals(640, $response['srcset'][2]['width']);
        $this->assertEquals(360, $response['srcset'][2]['height']);
        $this->assertEquals(0, strpos($response['srcset'][2]['url'], $this->_site->draftPath()));
        $this->assertEquals(320, $response['srcset'][3]['width']);
        $this->assertEquals(180, $response['srcset'][3]['height']);
        $this->assertEquals(0, strpos($response['srcset'][3]['url'], $this->_site->draftPath()));

        // Assert response ratio
        $this->assertArrayHasKey('ratio', $response);
        $this->assertTrue(is_double($response['ratio']));
        $this->assertEquals(1.7782909930716, $response['ratio']);

        // Assert images creation
        $this->assertTrue($this->_fs->has($response['srcset'][0]['url']));
        $this->assertTrue($this->_fs->has($response['srcset'][1]['url']));
        $this->assertTrue($this->_fs->has($response['srcset'][2]['url']));
        $this->assertTrue($this->_fs->has($response['srcset'][3]['url']));

        // Assert metadata last modification time are saved for images
        $metadata = $this->_site->loadMetadata();
        $path0 = substr($response['srcset'][0]['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path0, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path0]);
        $path1 = substr($response['srcset'][1]['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path1, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path1]);
        $path2 = substr($response['srcset'][2]['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path2, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path2]);
        $path3 = substr($response['srcset'][3]['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path3, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path3]);
    }

    public function testUploadExternalNoSrcParameter()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_image&action=uploadExternal');

        $this->assertEquals($client->getResponse()->getStatusCode(), 400);
    }

    public function testUploadExternal()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_image&action=uploadExternal', [
            'src' => $this->_externalSource
        ]);

        $this->assertValidJsonResponse($client);

        $response = json_decode($client->getResponse()->getContent(), true);

        $this->assertCount(3, $response);

        // Assert response status
        $this->assertArrayHasKey('status', $response);
        $this->assertEquals(0, $response['status']);

        // Assert response srcset
        $this->assertArrayHasKey('srcset', $response);
        $this->assertTrue(is_array($response['srcset']));
        $this->assertCount(2, $response['srcset']);
        $this->assertEquals(435, $response['srcset'][0]['width']);
        $this->assertEquals(111, $response['srcset'][0]['height']);
        $this->assertEquals(0, strpos($response['srcset'][0]['url'], $this->_site->draftPath()));
        $this->assertEquals(320, $response['srcset'][1]['width']);
        $this->assertEquals(82, $response['srcset'][1]['height']);
        $this->assertEquals(0, strpos($response['srcset'][1]['url'], $this->_site->draftPath()));

        // Assert response ratio
        $this->assertArrayHasKey('ratio', $response);
        $this->assertTrue(is_double($response['ratio']));
        $this->assertEquals(3.9189189189189, $response['ratio']);

        // Assert images creation
        $this->assertTrue($this->_fs->has($response['srcset'][0]['url']));
        $this->assertTrue($this->_fs->has($response['srcset'][1]['url']));

        // Assert metadata last modification time are saved for images
        $metadata = $this->_site->loadMetadata();
        $path0 = substr($response['srcset'][0]['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path0, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path0]);
        $path1 = substr($response['srcset'][1]['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path1, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path1]);
    }

    public function testImageNoImageParameter()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_image&action=image');

        $this->assertEquals($client->getResponse()->getStatusCode(), 400);
    }

    public function testImageNoDataParameter()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_image&action=image', [
            'image' => 'test-image.jpg',
        ]);

        $this->assertEquals($client->getResponse()->getStatusCode(), 400);
    }

    public function testImageSourceImageNotFound()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_image&action=image', [
            'image' => 'test-image.jpg',
            'data' => 'testdata'
        ]);

        $this->assertEquals($client->getResponse()->getStatusCode(), 400);
    }

    public function testImage()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        // Create dummy image we are working on
        $testImage = $this->_site->draftPath() . '/test-image-sc5704f5b8aa6a5.jpg';
        $this->_fs->put($testImage, file_get_contents('../test-content/images/dummy-1540x866-commodore64-plain-1280.jpg'));

        $client->request('POST', '/?service=_image&action=image', [
            'image' => $testImage,
            'data' => '0:0:50:50'
        ]);

        $this->assertValidJsonResponse($client);

        $response = json_decode($client->getResponse()->getContent(), true);

        $this->assertCount(3, $response);

        // Assert response status
        $this->assertArrayHasKey('status', $response);
        $this->assertEquals(0, $response['status']);

        // Assert response srcset
        $this->assertArrayHasKey('srcset', $response);
        $this->assertTrue(is_array($response['srcset']));
        $this->assertCount(2, $response['srcset']);
        $this->assertEquals(640, $response['srcset'][0]['width']);
        $this->assertEquals(360, $response['srcset'][0]['height']);
        $this->assertEquals(0, strpos($response['srcset'][0]['url'], $this->_site->draftPath()));
        $this->assertEquals(320, $response['srcset'][1]['width']);
        $this->assertEquals(180, $response['srcset'][1]['height']);
        $this->assertEquals(0, strpos($response['srcset'][1]['url'], $this->_site->draftPath()));

        // Assert response ratio
        $this->assertArrayHasKey('ratio', $response);
        $this->assertTrue(is_double($response['ratio']));
        $this->assertEquals(1.7777777777778001, $response['ratio']);

        // Assert images creation
        $this->assertTrue($this->_fs->has($response['srcset'][0]['url']));
        $this->assertTrue($this->_fs->has($response['srcset'][1]['url']));

        // Assert metadata last modification time are saved for images
        $metadata = $this->_site->loadMetadata();
        $path0 = substr($response['srcset'][0]['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path0, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path0]);
        $path1 = substr($response['srcset'][1]['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path1, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path1]);

    }
}