<?php
namespace Sitecake\Services\Content;

use Sitecake\Site;

class Content
{
	/**
	 * Storage manager
	 *
	 * @var Site
	 */
	protected $_site;

	/**
	 * Array containing all pages with path
	 *
	 * @var array
	 */
	protected $_pages;

	/**
	 * Indexed array where indexes are container names and
	 * values are arrays of Page objects that contain that specific container
	 *
	 * @var array
	 */
	protected $_containers;

	/**
	 * Content constructor.
	 *
	 * @param Site $site
	 */
	public function __construct($site)
	{
		$this->_site = $site;
	}

	public function save($data)
	{
		foreach ($data as $container => $content)
		{
			// remove slashes
			if (function_exists('get_magic_quotes_gpc') && get_magic_quotes_gpc())
			{
				$content = stripcslashes($content);
			}
			$content = base64_decode($content);
			$this->setContainerContent($container, $content);
		}
		$this->savePages();

		return 0;
	}

	protected function pages()
	{
		if (!$this->_pages)
		{
			$this->_pages = $this->_site->getAllPages();
		}

		return $this->_pages;
	}

	protected function containers()
	{
		if (!$this->_containers)
		{
			$this->initContainers();
		}

		return $this->_containers;
	}

	protected function initContainers()
	{
		$this->_containers = [];
		$pages = $this->pages();
		foreach ($pages as $page)
		{
			$pageContainers = $page['page']->containers();
			foreach ($pageContainers as $container)
			{
				if (array_key_exists($container, $this->_containers))
				{
					array_push($this->_containers[ $container ], $page);
				}
				else
				{
					$this->_containers[ $container ] = [$page];
				}
			}
		}
	}

	protected function setContainerContent($container, $content)
	{
		$containers = $this->containers();
		if (isset($containers[ $container ]))
		{
			foreach ($containers[ $container ] as $page)
			{
				$this->setPageDirty($page);
				$page['page']->setContainerContent($container, $content);
			}
		}
	}

	protected function setPageDirty($page)
	{
		foreach ($this->_pages as &$p)
		{
			if ($page['path'] === $p['path'])
			{
				$p['dirty'] = true;
			}
		}
	}

	protected function savePages()
	{
		foreach ($this->pages() as $page)
		{
			if (isset($page['dirty']) && $page['dirty'] === true)
			{
				$this->_site->savePage($page['path'], $page['page']);
			}
		}
	}
}