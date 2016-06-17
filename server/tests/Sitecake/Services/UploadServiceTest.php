<?php

namespace Sitecake\Services;

include_once 'Services_WebTestCase.php';

function fopen($file, $flag)
{
    return \fopen(__DIR__ . '/../../test-content/files/test.pdf', $flag);
}

class UploadServiceTest extends Services_WebTestCase
{
    public function testUploadNoXFilenameHeader()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_upload&action=upload');

        $this->assertEquals($client->getResponse()->getStatusCode(), 400);
    }

    public function testUploadWithInvalidExtension()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_upload&action=upload', [], [], [
            'HTTP_X-FILENAME' => base64_encode('test-file.php')
        ]);

        $this->assertValidJsonResponse($client);

        $response = json_decode($client->getResponse()->getContent(), true);

        // Assert response status
        $this->assertArrayHasKey('status', $response);
        $this->assertEquals(1, $response['status']);
    }

    public function testUpload()
    {
        // Edit session initialization
        $this->_site->startEdit();

        $client = $this->createClient();

        $client->request('POST', '/?service=_upload&action=upload', [], [], [
            'HTTP_X-FILENAME' => base64_encode('test.pdf')
        ]);

        $this->assertValidJsonResponse($client);

        $response = json_decode($client->getResponse()->getContent(), true);

        $this->assertCount(2, $response);

        // Assert response status
        $this->assertArrayHasKey('status', $response);
        $this->assertEquals(0, $response['status']);

        // Assert response url
        $this->assertArrayHasKey('url', $response);
        $this->assertTrue(is_string($response['url']));
        $this->assertEquals(0, strpos($response['url'], $this->_site->draftPath()));

        // Assert file creation
        $this->assertTrue($this->_fs->has($response['url']));

        // Assert metadata last modification time are saved for images
        $metadata = $this->_site->loadMetadata();
        $path = substr($response['url'], strlen($this->_site->draftPath() . '/'));
        $this->assertArrayHasKey($path, $metadata['files']);
        $this->assertArrayHasKey(1, $metadata['files'][$path]);
    }
}