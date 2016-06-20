<?php

namespace Sitecake\Filesystem;

class DeletePathsTest extends \PHPUnit_Framework_TestCase {
	
    public function tearDown() {
        \Mockery::close();
    }

	public function testHandle() {
		$fs = \Mockery::mock('League\Flysystem\Filesystem');

		$fs->shouldReceive('delete')
			->with('p1')
			->andReturn(true)
			->once();

		$fs->shouldReceive('delete')
			->with('p2')
			->andReturn(true)
			->once();

		$plugin = new DeletePaths();
		$plugin->setFilesystem($fs);

		$plugin->handle(array('p1', 'p2'));
	}
}