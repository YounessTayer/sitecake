<?php

namespace Sitecake\Filesystem;

class CopyPathsTest extends \PHPUnit_Framework_TestCase
{
	
    public function tearDown() {
        \Mockery::close();
    }

	public function testHandle() {
		$fs = \Mockery::mock('League\Flysystem\Filesystem');

		$fs->shouldReceive('getMetadata')
			->with('p1')
			->andReturn([
				'type' => 'file'
			])
			->once();

		$fs->shouldReceive('getMetadata')
			->with('p2')
			->andReturn([
				'type' => 'dir'
			])
			->once();

		$fs->shouldReceive('getMetadata')
			->with('p3')
			->andReturn([
				'type' => 'file'
			])
			->once();

		$fs->shouldReceive('getMetadata')
			->with('s1/p3')
			->andReturn([
				'type' => 'file'
			])
			->once();

		$fs->shouldReceive('has')
			->with('dpath/p1')
			->andReturn(false)
			->once();

		$fs->shouldReceive('has')
			->with('dpath/p3')
			->andReturn(true)
			->once();

		$fs->shouldReceive('copy')
			->with('p1', 'dpath/p1')
			->andReturn(true)
			->once();

		$fs->shouldReceive('copy')
			->with('p2', 'dpath/p2')
			->never();

		$fs->shouldReceive('copy')
			->with('p3', 'dpath/p3')
			->never();

		$fs->shouldReceive('copy')
			->with('s1/p3', 'dpath/p3')
			->andReturn(true)
			->never();

		$fs->shouldReceive('update')
			->with('dpath/p3', 'content')
			->andReturn(true)
			->once();

		$fs->shouldReceive('read')
			->with('s1/p3')
			->andReturn('content')
			->once();

		$plugin = new CopyPaths();
		$plugin->setFilesystem($fs);

		$plugin->handle(array('p3'), '', 'dpath', function($path) {
			return $path != 'p3';
		});
		$plugin->handle(array('p1', 'p2'), '', 'dpath');
		$plugin->handle(array('s1/p3'), 's1/', 'dpath');
	}
}