<?php

namespace Sitecake\Services\Pages;

use Sitecake\Services\Service;

class PagesService extends Service {

	const SERVICE_NAME = '_pages';

	public static function name()
	{
		return self::SERVICE_NAME;
	}

	protected $ctx;

	protected $pages;

	public function __construct($ctx)
	{
		$this->ctx = $ctx;
		$this->pages = new Pages($ctx['site'], $ctx);
	}

	public function pages($request)
	{
        $pageUpdates = $request->request->get('pages');
        if (!is_null($pageUpdates))
        {
            $this->pages->update(json_decode($pageUpdates, true));
        }

		return $this->json($request, array('status' => 0, 'pages' => array_values($this->pages->listPages())), 200);
	}
}