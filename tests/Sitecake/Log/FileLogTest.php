<?php

namespace Sitecake\Log\Engine;

function time()
{
    return '123456789';
}
function date()
{
    return '2016-01-01 23:23:23';
}

namespace Sitecake\Log;

use org\bovigo\vfs\vfsStreamFile;
use Sitecake\Log\Engine\FileLog;
use org\bovigo\vfs\vfsStream;
use org\bovigo\vfs\vfsStreamDirectory;

class FileLogTest extends \PHPUnit_Framework_TestCase
{
    /**
     * @var  vfsStreamDirectory
     */
    private $root;

    /**
     * Set up test environment
     */
    public function setUp()
    {
        $this->root = vfsStream::setup('root');
    }

    public function testLog()
    {
        $fs = \Mockery::mock('League\Flysystem\Filesystem');
        $fs->shouldReceive('has')
            ->with('sitecake-temp/logs/sc-debug.log')
            ->andReturn(false)
            ->once();
        $fs->shouldReceive('has')
            ->with('sitecake-temp/logs/sitecake.log')
            ->andReturn(false)
            ->once();
        $fs->shouldReceive('has')
            ->with('sitecake-temp/logs/custom.log')
            ->andReturn(false)
            ->once();
        $fs->shouldReceive('write')
            ->with('sitecake-temp/logs/sc-debug.log', '')
            ->andReturnUsing(function () {
                $this->root->addChild(new vfsStreamFile('sitecake-temp/logs/sc-debug.log'));
                return true;
            })
            ->once();
        $fs->shouldReceive('put')
            ->with('sitecake-temp/logs/sc-debug.log', '[2016-01-01 23:23:23] Debug: Test debug' . "\n")
            ->andReturnUsing(function () {
                $this->root->getChild('sitecake-temp/logs/sc-debug.log')
                    ->setContent('[2016-01-01 23:23:23] Debug: Test debug' . "\n");
                return true;
            })
            ->once();
        $fs->shouldReceive('write')
            ->with('sitecake-temp/logs/sitecake.log', '')
            ->andReturnUsing(function () {
                $this->root->addChild(new vfsStreamFile('sitecake-temp/logs/sitecake.log'));
                return true;
            })
            ->once();
        $fs->shouldReceive('put')
            ->with('sitecake-temp/logs/sitecake.log', '[2016-01-01 23:23:23] Error: Test error' . "\n")
            ->andReturnUsing(function () {
                $this->root->getChild('sitecake-temp/logs/sitecake.log')
                    ->setContent('[2016-01-01 23:23:23] Error: Test error' . "\n");
                return true;
            })
            ->once();
        $fs->shouldReceive('write')
            ->with('sitecake-temp/logs/custom.log', '')
            ->andReturnUsing(function () {
                $this->root->addChild(new vfsStreamFile('sitecake-temp/logs/custom.log'));
                return true;
            })
            ->once();
        $fs->shouldReceive('put')
            ->with('sitecake-temp/logs/custom.log',
                '[2016-01-01 23:23:23] Error: Test error custom' . "\n" .
                '[2016-01-01 23:23:23] Error: Test error custom' . "\n"
            )
            ->andReturnUsing(function () {
                $this->root->getChild('sitecake-temp/logs/custom.log')
                    ->setContent(
                        '[2016-01-01 23:23:23] Error: Test error custom' . "\n" .
                        '[2016-01-01 23:23:23] Error: Test error custom' . "\n"
                    );
                return true;
            })
            ->once();
        $fs->shouldReceive('read')
            ->with('sitecake-temp/logs/sitecake.log')
            ->andReturn('')
            ->once();
        $fs->shouldReceive('read')
            ->with('sitecake-temp/logs/sc-debug.log')
            ->andReturn('')
            ->once();
        $fs->shouldReceive('read')
            ->with('sitecake-temp/logs/custom.log')
            ->andReturn('[2016-01-01 23:23:23] Error: Test error custom' . "\n")
            ->once();
        $fs->shouldReceive('ensureDir')
            ->with('sitecake-temp/logs')
            ->andReturn(true)
            ->once();
        $fs->shouldReceive('getMetadata')
            ->with('sitecake-temp/logs/sitecake.log')
            ->andReturn(['size' => 2097153])
            ->once();
        $fs->shouldReceive('getMetadata')
            ->with('sitecake-temp/logs/sc-debug.log')
            ->andReturn(['size' => 2097151])
            ->once();
        $fs->shouldReceive('getMetadata')
            ->with('sitecake-temp/logs/custom.log')
            ->andReturn(['size' => 2097153])
            ->once();
        $fs->shouldReceive('rename')
            ->with('sitecake-temp/logs/sitecake.log', 'sitecake-temp/logs/sitecake.log.123456789')
            ->andReturnUsing(function () {
                $this->root->addChild(new vfsStreamFile('sitecake-temp/logs/sitecake.log.123456789'));
                return true;
            })
            ->once();
        $fs->shouldReceive('delete')
            ->with('sitecake-temp/logs/custom.log')
            ->andReturn(true)
            ->once();

        $this->assertFalse($this->root->hasChild('sitecake/logs'));
        $logger = new FileLog($fs, [
            'log.size' => '2MB',
            'log.archive_size' => 2,
            'debug' => true
        ]);

        $this->assertFalse($this->root->hasChild('sitecake-temp/logs/sitecake.log'));
        $logger->log('error', 'Test error');
        $this->assertTrue($this->root->hasChild('sitecake-temp/logs/sitecake.log'));
        $this->assertTrue($this->root->hasChild('sitecake-temp/logs/sitecake.log.123456789'));
        $this->assertEquals('[2016-01-01 23:23:23] Error: Test error',
            trim($this->root->getChild('sitecake-temp/logs/sitecake.log')->getContent()));

        $this->assertFalse($this->root->hasChild('sitecake-temp/logs/sc-debug.log'));
        $logger->log('debug', 'Test debug');
        $this->assertTrue($this->root->hasChild('sitecake-temp/logs/sc-debug.log'));
        $this->assertRegExp('/\[\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\] Debug: Test debug/',
            trim($this->root->getChild('sitecake-temp/logs/sc-debug.log')->getContent()));

        $logger = new FileLog($fs, [
            'log.size' => '2MB',
            'log.archive_size' => 0,
            'log.path' => 'sitecake-temp/logs/custom.log'
        ]);

        $this->assertFalse($this->root->hasChild('sitecake-temp/logs/custom.log'));
        $logger->log('error', 'Test error custom');
        $this->assertTrue($this->root->hasChild('sitecake-temp/logs/custom.log'));
        $this->assertFalse($this->root->hasChild('sitecake-temp/logs/custom.log.123456789'));
        $this->assertEquals('[2016-01-01 23:23:23] Error: Test error custom' . "\n" .
                            '[2016-01-01 23:23:23] Error: Test error custom',
            trim($this->root->getChild('sitecake-temp/logs/custom.log')->getContent()));
    }
}