<?php

namespace Sitecake\Filesystem;

class RandomDirectoryTest extends \PHPUnit_Framework_TestCase {
	
    public function teardown() {
        \Mockery::close();
    }

	public function testHandleExisting() {
		$fs = \Mockery::mock('League\Flysystem\Filesystem');

		$fs->shouldReceive('listPatternPaths')
			->with('d1', '/^.*\/r[0-9a-f]{13}$/')
			->andReturn(array('d1/r1234567890123'))
			->once();

		$plugin = new RandomDirectory();
		$plugin->setFilesystem($fs);

		$this->assertEquals('d1/r1234567890123', $plugin->handle('d1'));
	}

	public function testHandleNew() {
		$fs = \Mockery::mock('League\Flysystem\Filesystem');

		$fs->shouldReceive('listPatternPaths')
			->with('d2', '/^.*\/r[0-9a-f]{13}$/')
			->andReturn(array())
			->once();

		$fs->shouldReceive('ensureDir')
			->with('/^d2\/r[0-9a-f]{13}$/')
			->andReturn(true)
			->once();

		$plugin = new RandomDirectory();
		$plugin->setFilesystem($fs);

		$this->assertEquals(1, preg_match('/^d2\/r[0-9a-f]{13}$/', $plugin->handle('d2')));
	}
}