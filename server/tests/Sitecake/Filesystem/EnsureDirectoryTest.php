<?php

namespace Sitecake\Filesystem;

class EnsureDirectoryTest extends \PHPUnit_Framework_TestCase {
	
    public function tearDown() {
        \Mockery::close();
    }

	public function testHandle() {
		$fs = \Mockery::mock('League\Flysystem\Filesystem');

		$fs->shouldReceive('has')
			->with('d1')
			->andReturn(false)
			->once();

		$fs->shouldReceive('createDir')
			->with('d1')
			->andReturn(true)
			->once();						

		$fs->shouldReceive('has')
			->with('d2')
			->andReturn(false)
			->once();

		$fs->shouldReceive('createDir')
			->with('d2')
			->andReturn(false)
			->once();

		$plugin = new EnsureDirectory();
		$plugin->setFilesystem($fs);

		$this->assertEquals('d1', $plugin->handle('d1'));
		$this->assertFalse($plugin->handle('d2'));
	}
}