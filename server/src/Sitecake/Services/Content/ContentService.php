<?php

namespace Sitecake\Services\Content;

use Silex\Application;
use Sitecake\Exception\Http\BadRequestException;
use Sitecake\Services\Service;
use Sitecake\Site;

class ContentService extends Service
{
	/**
	 * @var Site
	 */
	protected $_site;

	/**
	 * @var Content
	 */
	protected $_content;

	public function __construct($ctx)
	{
		$this->_site = $ctx['site'];
		$this->_content = new Content($this->_site);
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
		$this->_site->publishDraft();

		return $this->json($request, ['status' => 0]);
	}

}