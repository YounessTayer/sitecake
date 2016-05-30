<?php

namespace Sitecake\Services\Content;

use Silex\Application;
use Sitecake\Exception\Http\BadRequestException;
use Sitecake\Services\Service;

class ContentService extends Service
{
	/**
	 * @var Application
	 */
	protected $_ctx;

	/**
	 * @var Content
	 */
	protected $_content;

	public function __construct($ctx)
	{
		$this->_ctx = $ctx;
		$this->_content = new Content($ctx['site']);
	}

	public function save($request)
	{
		$id = $request->request->get('scpageid');
		if (is_null($id))
		{
			throw new BadRequestException('Page ID is missing');
		}

		$request->request->remove('scpageid');

		$this->_content->save($request->request->all());

		return $this->json($request, ['status' => 0]);
	}

	public function publish($request)
	{
		$this->_ctx['site']->publishDraft();

		return $this->json($request, ['status' => 0]);
	}

}