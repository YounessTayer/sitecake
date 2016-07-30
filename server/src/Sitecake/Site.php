<?php
namespace Sitecake;

use League\Flysystem\Directory;
use LogicException;
use RuntimeException;
use League\Flysystem\FilesystemInterface;
use Sitecake\Exception\FileNotFoundException;
use Sitecake\Exception\InternalException;
use Sitecake\Util\Utils;

class Site
{
    const RESOURCE_TYPE_ALL = 'all';
    const RESOURCE_TYPE_PAGE = 'page';
    const RESOURCE_TYPE_RESOURCE = 'resource';
    const RESOURCE_TYPE_IMAGE = 'image';
    const RESOURCE_TYPE_FILE = 'file';

    protected $_config;

    /**
     * @var FilesystemInterface
     */
    protected $_fs;

    protected $_tmp;

    protected $_draft;

    protected $_backup;

    protected $_ignores;

    protected $_pageFiles;

    /**
     * Metadata that are stored in draft marker file.
     * Contains next information :
     *      + lastPublished : Timestamp when content was published last time
     *      + files : All site file paths with respective modification times for public [0] and draft [1] versions
     *      + pages : All page file paths with its details :
     *          * id - page id. The service should use the page id to identify and update an appropriate existing page,
     *                 even if its url/path has been changed.
     *          * url - website root/website relative URL/file path.
     *          * idx - nav bar index. -1 if not present in the nav bar or relative position within the nav bar.
     *          * title - page title. Content of the <title> tag.
     *          * navtitle - title used in the nav element.
     *          * desc - meta description. Content of the meta description tag.
     *      + menus : Contain paths of files that contains menu(s) [pages] and list of menu items [items]
     * @var array
     */
    protected $_metadata;

    protected $_defaultMetadataStructure = [
        'lastPublished' => 0,
        'files' => [],
        'pages' => [],
        'menus' => []
    ];

    public function __construct(FilesystemInterface $fs, $config)
    {
        $this->_config = $config;
        $this->_fs = $fs;

        $this->__ensureDirs();

        $this->_ignores = [];
        $this->__loadIgnorePatterns();

        $this->loadMetadata();
    }

    private function __ensureDirs()
    {
        // check/create directory images
        try
        {
            if (!$this->_fs->ensureDir('images'))
            {
                throw new LogicException('Could not ensure that the directory /images is present and writtable.');
            }
        }
        catch (RuntimeException $e)
        {
            throw new LogicException('Could not ensure that the directory /images is present and writtable.');
        }
        // check/create files
        try
        {
            if (!$this->_fs->ensureDir('files'))
            {
                throw new LogicException('Could not ensure that the directory /files is present and writtable.');
            }
        }
        catch (RuntimeException $e)
        {
            throw new LogicException('Could not ensure that the directory /files is present and writtable.');
        }
        // check/create sitecake-temp
        try
        {
            if (!$this->_fs->ensureDir('sitecake-temp'))
            {
                throw new LogicException('Could not ensure that the directory /sitecake-temp is present and writable.');
            }
        }
        catch (RuntimeException $e)
        {
            throw new LogicException('Could not ensure that the directory /sitecake-temp is present and writable.');
        }
        // check/create sitecake-temp/<workid>
        try
        {
            $work = $this->_fs->randomDir('sitecake-temp');
            if ($work === false)
            {
                throw new LogicException('Could not ensure that the work directory in /sitecake-temp is present and writtable.');
            }
        }
        catch (RuntimeException $e)
        {
            throw new LogicException('Could not ensure that the work directory in /sitecake-temp is present and writtable.');
        }
        // check/create sitecake-temp/<workid>/tmp
        try
        {
            $this->_tmp = $this->_fs->ensureDir($work . '/tmp');
            if ($this->_tmp === false)
            {
                throw new LogicException('Could not ensure that the directory ' . $work .
                                         '/tmp is present and writtable.');
            }
        }
        catch (RuntimeException $e)
        {
            throw new LogicException('Could not ensure that the directory ' . $work . '/tmp is present and writtable.');
        }
        // check/create sitecake-temp/<workid>/draft
        try
        {
            $this->_draft = $this->_fs->ensureDir($work . '/draft');
            if ($this->_draft === false)
            {
                throw new LogicException('Could not ensure that the directory ' . $work .
                                         '/draft is present and writtable.');
            }
        }
        catch (RuntimeException $e)
        {
            throw new LogicException('Could not ensure that the directory ' . $work .
                                     '/draft is present and writtable.');
        }

        // check/create sitecake-backup
        try
        {
            if (!$this->_fs->ensureDir('sitecake-backup'))
            {
                throw new LogicException('Could not ensure that the directory /sitecake-backup is present and writtable.');
            }
        }
        catch (RuntimeException $e)
        {
            throw new LogicException('Could not ensure that the directory /sitecake-backup is present and writtable.');
        }
        // check/create sitecake-backup/<workid>
        try
        {
            $this->_backup = $this->_fs->randomDir('sitecake-backup');
            if ($work === false)
            {
                throw new LogicException('Could not ensure that the work directory in /sitecake-backup is present and writtable.');
            }
        }
        catch (RuntimeException $e)
        {
            throw new LogicException('Could not ensure that the work directory in /sitecake-backup is present and writtable.');
        }
    }

    private function __loadIgnorePatterns()
    {
        if ($this->_fs->has('.scignore'))
        {
            $this->_ignores = preg_split('/\R/', $this->_fs->read('.scignore'));
        }
        $this->_ignores = array_filter(array_merge($this->_ignores, [
            '.scignore',
            '.scpages',
            'draft.drt',
            'draft.mkr',
            $this->_config['entry_point_file_name'],
            'sitecake/',
            'sitecake-temp/',
            'sitecake-backup/'
        ]));
    }

    /**
     * Returns list of paths of Page files.
     *
     * Files that are considered as Page files are by default all files with valid extensions from root directory
     * and all files stated in .scpages file if it's present.
     * Files from root directory that shouldn't be considered as Page files can be filtered out
     * by stating them inside .scpages prefixed with exclamation mark (!)
     * If directory is stated in .scpages file all files from that directory are considered as Page files
     *
     * @return array
     */
    public function loadPageFilePaths()
    {
        if ($this->_pageFiles)
        {
            return $this->_pageFiles;
        }

        // Get valid page file extensions
        $extensions = $this->getValidPageExtensions();

        // List all pages in document root
        $pageFilePaths = $this->_fs->listPatternPaths('',
            '/^.*\.(' . implode('|', $extensions) . ')?$/');

        // Filter out entry point file
        if(($index = array_search($this->_config['entry_point_file_name'], $pageFilePaths)) !== false)
        {
            array_splice($pageFilePaths, $index, 1);
            $pageFilePaths = array_values($pageFilePaths);
        }

        // If .scpages file present we need to add page files stated inside and filter out ones that starts with !
        if ($this->_fs->has('.scpages'))
        {
            $scPages = $this->_fs->read('.scpages');

            if(!empty($scPages))
            {
                // Load page life paths from .scpages
                $scPagePaths = preg_split('/\R/', $this->_fs->read('.scpages'));

                foreach ($scPagePaths as $no => $path)
                {
                    if(empty($path))
                    {
                        continue;
                    }

                    // Filter out pages that starts with !
                    if (strpos($path, '!') === 0)
                    {
                        if (($index = array_search(substr($path, 1), $pageFilePaths)) !== false)
                        {
                            unset($pageFilePaths[ $index ]);
                        }

                        unset($scPagePaths[ $no ]);
                    }

                    // Read directory pages if directory is passed in .scpages
                    if (($noTrailingSlash = $path[ strlen($path) - 1 ]) == '/')
                    {
                        $pageFilePaths = array_merge($scPagePaths, $this->_fs->listPatternPaths(
                            $noTrailingSlash,
                            '/^.*\.(' . implode('|', $extensions) . ')?$/'
                        ));
                    }
                }

                // Merge root page files with page files from .scpages
                $pageFilePaths = array_merge($scPagePaths, $pageFilePaths);
            }
        }
        
        $this->_pageFiles = $pageFilePaths;

        return $this->_pageFiles;
    }

    public function isPageFile($path)
    {
        return in_array($path, $this->_pageFiles);
    }

    public function getValidPageExtensions()
    {
        $defaultPages = is_array($this->_config['site.default_pages']) ?
            $this->_config['site.default_pages'] :
            [$this->_config['site.default_pages']];

        return Utils::map(function ($pageName)
        {
            $nameParts = explode('.', $pageName);
            return array_pop($nameParts);
        }, $defaultPages);
    }

    /**
     * Returns the path of the temporary directory.
     * @return string the tmp dir path
     */
    public function tmpPath()
    {
        return $this->_tmp;
    }

    /**
     * Returns the path of the draft directory.
     * @return string the draft dir path
     */
    public function draftPath()
    {
        return $this->_draft;
    }

    protected function _draftBaseUrl()
    {
        return $this->draftPath() . '/';
    }

    protected function _stripDraftPath($path)
    {
        return substr($path, strlen($this->_draftBaseUrl()));
    }

    /**
     * Returns the path of the backup directory.
     * @return string the backup dir path
     */
    public function backupPath()
    {
        return $this->_backup;
    }

    public function pageFileUrl($path)
    {
        $base = $this->_base();
        if(!empty($this->_config['pages.use_document_relative_paths']) && strpos($path, $base) !== 0)
        {
            return $this->_base() . $path;
        }

        return $path;
    }

    /**
     * Strips / and . in front of url
     * @param $url
     *
     * @return mixed
     */
    public function pageFilePath($url)
    {
        return ltrim($url, './');
    }

    /**
     * Returns base dir for website
     * e.g. if site is under http://www.sitecake.com/demo method will return /demo/
     *
     * @return string
     */
    protected function _base()
    {
        $self = $_SERVER['PHP_SELF'];

        if(strpos($self, '/' . $this->_config['SERVICE_URL']) === 0)
        {
            $base = str_replace('/' . $this->_config['SERVICE_URL'], '', $self);
        }
        else
        {
            $base = dirname($self);
        }

        $base = preg_replace('#/+#', '/', $base);

        if ($base === DIRECTORY_SEPARATOR || $base === '.')
        {
            $base = '';
        }
        $base = implode('/', array_map('rawurlencode', explode('/', $base)));

        return $base . '/';
    }

    public function getBase()
    {
        if(!empty($this->_config['pages.use_document_relative_paths']))
        {
            return $this->_base();
        }

        return '';
    }

    /**
     * Returns passed page url modified by stripping base dir if it exists
     * @param string $url
     *
     * @return string Passed url stripped by base dir if found
     */
    public function stripBase($url)
    {
        if(strpos('/' . $url, $this->_base()) === 0)
        {
            return substr('/' . $url, strlen($this->_base()));
        }

        return $url;
    }

    /**
     * Returns a list of paths of CMS related files from the given
     * directory. It looks for HTML files, images and uploaded files.
     * It ignores entries from .scignore filter the output list.
     *
     * @param  string $directory the root directory to start search into
     * @param  string $type      Indicates what type of resources should be listed (pages, resources or all)
     *
     * @return array            the output paths list
     */
    public function listScPaths($directory = '', $type = self::RESOURCE_TYPE_ALL)
    {
        $ignores = $this->_ignores;

        return array_filter(array_merge(
            (in_array($type, [self::RESOURCE_TYPE_ALL, self::RESOURCE_TYPE_PAGE]) ?
                $this->_findSourceFiles($directory) : []),
            (in_array($type, [self::RESOURCE_TYPE_ALL, self::RESOURCE_TYPE_RESOURCE, self::RESOURCE_TYPE_IMAGE]) ?
                $this->_fs->listPatternPaths(ltrim($directory . '/images', '/'), '/^.*\-sc[0-9a-f]{13}[^\.]*\..+$/', true) : []),
            (in_array($type, [self::RESOURCE_TYPE_ALL, self::RESOURCE_TYPE_RESOURCE, self::RESOURCE_TYPE_FILE]) ?
                $this->_fs->listPatternPaths(ltrim($directory . '/files', '/'), '/^.*\-sc[0-9a-f]{13}[^\.]*\..+$/', true) : [])),
            function ($path) use ($ignores, $directory)
            {
                foreach ($ignores as $ignore)
                {
                    if (strpos($directory, $ignore) === 0)
                    {
                        continue;
                    }
                    if ($ignore !== '' && strpos($path, $ignore) === 0)
                    {
                        return false;
                    }
                }

                return true;
            });
    }

    protected function _findSourceFiles($directory)
    {
        // Get valid page file extensions
        $extensions = $this->getValidPageExtensions();

        // List first level files for passed directory
        $firstLevel = $this->_fs->listContents($directory);

        $paths = [];

        foreach ($firstLevel as $file)
        {
            if (($file['type'] == 'dir' && !in_array($file['path'] . '/', $this->_ignores)) ||
                ($file['type'] == 'file' && !in_array($file['basename'], $this->_ignores) &&
                 preg_match('/^.*\.(' . implode('|', $extensions) . ')?$/', $file['basename']) === 1)
            )
            {
                if ($file['type'] == 'dir')
                {
                    $paths = array_merge(
                        $paths,
                        $this->_fs->listPatternPaths($file['path'], '/^.*\.(' . implode('|', $extensions) . ')?$/', true)
                    );
                }
                else
                {
                    $paths[] = $file['path'];
                }
            }
        }

        return $paths;
    }

    /**
     * Check if passed path is resource
     *
     * @param string $path
     *
     * @return int
     */
    public function isResourcePath($path)
    {
        return (bool)preg_match('/^.*(files|images)\/.*\-sc[0-9a-f]{13}[^\.]*\..+$/', $path);
    }

    /**
     * Returns a list of CMS related page file paths from the
     * given directory.
     *
     * @param  string $directory a directory to read from
     *
     * @return array            a list of page file paths
     */
    public function listScPagesPaths($directory = '')
    {
        return $this->listScPaths($directory, self::RESOURCE_TYPE_PAGE);
    }

    /**
     * Returns a list of CMS related resource file paths from the
     * given directory.
     *
     * @param  string $directory Directory to read from
     *
     * @return array List of resource file paths
     */
    public function listPublicResources($directory = '')
    {
        return $this->listScPaths($directory, self::RESOURCE_TYPE_RESOURCE);
    }

    /**
     * Returns a list of draft page file paths.
     *
     * @return array a list of draft page file paths
     */
    public function listDraftPagePaths()
    {
        return $this->listScPaths($this->draftPath(), self::RESOURCE_TYPE_PAGE);
    }

    /**
     * Starts the site draft out of the public content.
     * It copies public pages and resources into the draft folder.
     */
    public function startEdit()
    {
        $this->loadPageFilePaths();

        if (!$this->draftExists())
        {
            $this->startDraft();
        }
        else
        {
            $this->cleanupDraft();
            $this->updateFromSource();
        }
    }

    public function restore($version = 0)
    {

    }

    public function getDefaultPage($directory = '')
    {
        return new Page($this->_fs->read($this->getDefaultIndex($directory)));
    }

    public function getDefaultIndex($directory = '')
    {
        $paths = $this->listScPagesPaths($directory);

        $defaultPages = is_array($this->_config['site.default_pages']) ?
            $this->_config['site.default_pages'] :
            [$this->_config['site.default_pages']];
        foreach ($defaultPages as $defaultPage)
        {
            $dir = $directory ? rtrim($directory, '/') . '/' : '';
            if (in_array($dir . $defaultPage, $paths))
            {
                return $dir . $defaultPage;
            }
        }

        throw new FileNotFoundException([
            'type' => 'Default page',
            'files' => '[' . implode(', ', $defaultPages) . ']'
        ], 401);

    }

    public function getDefaultPublicPage()
    {
        $publicPagePaths = $this->listScPagesPaths();
        $pagePath = $this->getDefaultIndex();
        if (in_array($pagePath, $publicPagePaths))
        {
            $draft = new Draft($this->_fs->read($pagePath));

            // Normalize resource URLs
            $draft->normalizeResourcePaths($this->_base());

            // Set Page ID stored in metadata if exists
            if(!($pageID = $this->_getPageID($pagePath)))
            {
                $pageID = Utils::id();
            }
            $draft->setPageId($pageID);
            $draft->addRobotsNoIndexNoFollow();

            return $draft;
        }
        else
        {
            throw new FileNotFoundException([
                'type' => 'Default page',
                'files' => $pagePath
            ], 401);
        }
    }

    public function getDefaultDraftPage()
    {
        return $this->getDefaultPage($this->draftPath());
    }

    public function getDraft($uri)
    {
        $draftPagePaths = $this->listDraftPagePaths();
        $currentWorkingDir = getcwd();
        $executionDirectory = $this->_draftBaseUrl();

        $uri = $this->stripBase($uri);

        if (!empty($uri))
        {
            $pagePath = $this->_draftBaseUrl() . $uri;

            if ($this->_fs->has($pagePath) && $this->_fs->get($pagePath) instanceof Directory)
            {
                $pagePath = $this->getDefaultIndex($pagePath);
            }
        }
        else
        {
            $pagePath = $this->getDefaultIndex($this->draftPath());
        }

        // Check if we need to change execution directory
        if ($dir = implode('/', array_slice(explode('/', $pagePath), 0, -1)))
        {
            $executionDirectory = $dir;
        }

        if (in_array($pagePath, $draftPagePaths))
        {
            // Move execution to directory where requested page is because of php includes
            chdir($this->_fs->getAdapter()->applyPathPrefix($executionDirectory));

            $draft = new Draft($this->_fs->read($pagePath));

            // Normalize resource URLs
            $draft->normalizeResourcePaths($this->_base());

            // Set Page ID stored in metadata
            $draft->setPageId($this->_getPageID($pagePath));

            // Add robots meta tag
            $draft->addRobotsNoIndexNoFollow();

            // Turn execution back to root dir
            chdir($currentWorkingDir);

            return $draft;
        }
        else
        {
            throw new FileNotFoundException([
                'type' => 'Draft Page',
                'files' => $pagePath
            ], 401);
        }
    }

    public function getAllPages()
    {
        $pages = [];
        $draftPagePaths = $this->listDraftPagePaths();
        foreach ($draftPagePaths as $pagePath)
        {
            array_push($pages, [
                'path' => $pagePath,
                'page' => new Page($this->_fs->read($pagePath))
            ]);
        }

        return $pages;
    }

    public function savePage($path, Page $page)
    {
        $this->loadMetadata();
        $this->markDraftDirty();
        $this->_fs->update($path, (string)$page);
        $this->saveLastModified($path);
    }

    public function updatePage($path, $pageDetails)
    {
        $path = $this->_draftBaseUrl() . $path;
        if(!$this->_fs->has($path))
        {
            throw new FileNotFoundException([
                'type' => 'Source Page',
                'file' => $path
            ], 401);
        }

        $page = new Page($this->_fs->read($path));

        $page->setPageTitle($pageDetails['title']);
        $page->setPageDescription($pageDetails['desc']);

        return $page;
    }

    public function updatePageFiles($pages, $pagesMetadata)
    {
        // Update page files
        foreach ($pages as $page)
        {
            $path = $this->_draftBaseUrl() . $page['path'];

            if($this->_fs->has($path))
            {
                $this->_fs->update($path, (string)$page['page']);
            }
            else
            {
                $this->_fs->write($path, (string)$page['page']);
            }
            // Update last modified time in metadata
            $this->saveLastModified($path);
        }

        // Update menus
        $this->_updateMenus($pagesMetadata);
        // Save metadata
        $this->savePagesMetadata($pagesMetadata);
        // Publish draft
        $this->publishDraft();
    }

    public function newPage($path, $sourcePage)
    {
        $metadata = $this->loadMetadata();

        $sourcePath = '';
        foreach ($metadata['pages'] as $page)
        {
            if($page['id'] == $sourcePage['tid'])
            {
                $sourcePath = $this->pageFilePath($page['url']);
                break;
            }
        }

        $draftPath = $this->_draftBaseUrl() . $sourcePath;

        if(empty($sourcePath) || !$this->_fs->has($draftPath))
        {
            throw new FileNotFoundException([
                'type' => 'Source Page',
                'files' => $sourcePath
            ], 401);
        }

        $page = new Page($this->_fs->read($draftPath));

        // Clear old container names
        $page->cleanupContainerNames();
        // Name unnamed containers
        $page->normalizeContainerNames();
        // Check for existing navigation in current page and store it if found
        if($this->hasMenu($page))
        {
            $this->_findMenus($page, $path);
        }

        // Duplicate resources from unnamed containers
        $resources = $page->listResourceUrls(function($container) use ($page) {
            return $page->isUnnamedContainer($container);

        });
        $sets = [];
        foreach($resources as $resource)
        {
            $resourceDetails = Utils::resurlinfo($resource);
            if(array_key_exists($resourceDetails['id'], $sets))
            {
                $id = $sets[$resourceDetails['id']];
            }
            else
            {
                $id = uniqid();
                $sets[$resourceDetails['id']] = $id;
            }
            $newPath = Utils::resurl($resourceDetails['path'], $resourceDetails['name'], $id,
                $resourceDetails['subid'], $resourceDetails['ext']);
            $this->_fs->put($newPath, $this->_fs->read($resource));
            $page->updateResourcePath($resource, $newPath);
        }

        $page->setPageTitle($sourcePage['title']);
        $page->setPageDescription($sourcePage['desc']);

        return $page;
    }

    public function deleteDraftPages($paths)
    {
        foreach ($paths as $path)
        {
            $path = $this->_draftBaseUrl() . $path;
            $this->_fs->delete($path);
        }
    }

    public function publishDraft()
    {
        if ($this->draftExists())
        {
            $this->backup();

            // Get all draft pages with all draft files referenced in those pages
            $draftResources = $this->draftResources();
            // Get public resource paths so we can check if there are any public resources that should be deleted
            $publicResources = $this->listScPaths();

            foreach($draftResources as $no => $file)
            {
                // Overwrite live file with draft only if draft actually exists
                if($this->_fs->has($file))
                {
                    $publicPath = $this->_stripDraftPath($file);
                    if($this->_fs->has($publicPath))
                    {
                        $this->_fs->delete($publicPath);
                        array_splice($publicResources, array_search($publicPath, $publicResources), 1);
                    }
                    $this->_fs->copy($file, $publicPath);
                }
            }
            
            if(!empty($publicResources))
            {
                $this->_fs->deletePaths($publicResources);
            }

            $this->cleanupPublic();
            $this->saveLastPublished();
            $this->markDraftClean();
        }
    }

    public function isDraftClean()
    {
        return !$this->_fs->has($this->draftDirtyMarkerPath());
    }

    public function markDraftDirty()
    {
        if (!$this->_fs->has($this->draftDirtyMarkerPath()))
        {
            $this->_fs->write($this->draftDirtyMarkerPath(), '');
        }
    }

    public function markDraftClean()
    {
        if ($this->_fs->has($this->draftDirtyMarkerPath()))
        {
            $this->_fs->delete($this->draftDirtyMarkerPath());
        }
    }

    protected function draftDirtyMarkerPath()
    {
        return $this->draftPath() . '/draft.drt';
    }

    protected function draftMarkerPath()
    {
        return $this->draftPath() . '/draft.mkr';
    }

    protected function draftExists()
    {
        return $this->_fs->has($this->draftMarkerPath());
    }

    /**
     * Retrieves site metadata from file. Also stores internal _metadata property.
     *
     * @throws InternalException If metadata written to file can't be unserialized
     *
     * @return array Metadata loaded from file if it exists or default empty metadata structure
     */
    public function loadMetadata()
    {
        if (!$this->_metadata)
        {
            if ($this->draftExists())
            {
                $this->_metadata = @unserialize($this->_fs->read($this->draftMarkerPath()));

                if ($this->_metadata === null)
                {
                    throw new InternalException('Metadata could\'t be unserialized');
                }

                if(empty($this->_metadata))
                {
                    $this->_metadata = $this->_defaultMetadataStructure;
                }
            }
            else
            {
                $this->_metadata = $this->_defaultMetadataStructure;
            }
        }

        return $this->_metadata;
    }

    public function saveLastModified($path)
    {
        $this->loadMetadata();

        $index = 0;

        $filePath = $path;

        if(strpos($path, $this->draftPath()) === 0)
        {
            $filePath = $this->_stripDraftPath($path);
            $index = 1;
        }

        if(!isset($this->_metadata['files'][$filePath]))
        {
            $this->_metadata['files'][$filePath] = [];
        }

        $meta = $this->_fs->getMetadata($path);

        $this->_metadata['files'][$filePath][$index] = $meta['timestamp'];

        $this->writeMetadata();
    }

    public function saveLastPublished()
    {
        $this->loadMetadata();

        $this->_metadata['lastPublished'] = time();

        $this->writeMetadata();
    }

    public function savePagesMetadata($pages)
    {
        $this->loadMetadata();
        $this->_metadata['pages'] = $pages;
        $this->writeMetadata();
    }

    /**
     * Writes site metadata to file.
     *
     * @return bool Operation success
     */
    public function writeMetadata()
    {
        if($this->draftExists())
        {
            return $this->_fs->update($this->draftMarkerPath(), serialize($this->_metadata));
        }
        return $this->_fs->write($this->draftMarkerPath(), serialize($this->_metadata));
    }

    /**
     * Gets page ID for specific page from metadata if it exist, if not returns false
     * @param $path
     *
     * @return int|bool
     */
    protected function _getPageID($path)
    {
        $this->loadMetadata();

        if(!isset($this->_metadata['pages'][$this->_stripDraftPath($path)]))
        {
            return false;
        }

        return $this->_metadata['pages'][$this->_stripDraftPath($path)]['id'];
    }

    /**
     * Starts site draft. Copies all pages and resources to draft directory.
     * Also prepares container names, prefixes all urls in draft pages
     * and collects all navigation sections that appears inside pages.
     */
    protected function startDraft()
    {
        // Copy and prepare all resources and pages (normalize containers and prefix resource urls) and load navigation
        $paths = $this->listScPaths();
        foreach ($paths as $path)
        {
            $this->_createDraftResource($path);
        }

        $this->_updateMenuMetadata();

        // Set metadata
        $this->writeMetadata();
    }

    public function updateFromSource()
    {
        // Check if draft is clean
        $isDraftClean = $this->isDraftClean();

        // Get all draft page files to able to compare and delete files that don't exist any more
        $draftPaths = $this->listScPaths($this->draftPath());

        // Overwrite outdated resources
        $paths = $this->listScPaths();
        foreach ($paths as $path)
        {
            $draftPath = $this->_draftBaseUrl() . $path;

            // Filter out draft path from all draft paths
            if(($index = array_search($draftPath, $draftPaths)) !== false)
            {
                unset($draftPaths[$index]);
            }

            if (!$this->_fs->has($draftPath))
            {
                // This is a new resource/page and should be copied to draft
                $this->_createDraftResource($path);
            }
            else
            {
                // Get public file metadata
                $pageMetadata = $this->_fs->getMetadata($path);

                if (isset($this->_metadata['files'][ $path ][0]))
                {
                    // Check last modification time for resource and overwrite draft file if it is needed and possible
                    if ($pageMetadata['timestamp'] > $this->_metadata['files'][ $path ][0])
                    {
                        if(!$this->isResourcePath($path))
                        {
                            // Initialize Page to check if it's editable or has menus
                            $page = new Page($this->_fs->read($path));

                            if($page->isEditable() || $this->hasMenu($page))
                            {
                                if ($isDraftClean || $this->_config['pages.prioritize_manual_changes'])
                                {
                                    $this->_fs->delete($draftPath);
                                    $this->_createDraftResource($path);
                                }
                            }
                            else
                            {
                                $this->_fs->delete($draftPath);
                                $this->_createDraftResource($path);
                            }
                        }
                        else
                        {
                            $this->_fs->delete($draftPath);
                            $this->_fs->copy($path, $draftPath);
                        }
                    }
                }
                else
                {
                    $draftMetadata = $this->_fs->getMetadata($draftPath);
                    if($isDraftClean || ($this->_metadata['lastPublished'] > $draftMetadata['timestamp']))
                    {
                        $this->_fs->delete($draftPath);
                        $this->_createDraftResource($path);
                    }

                    // Remember last modification times
                    $this->_metadata['files'][ $path ] = [
                        $pageMetadata['timestamp'],
                        $draftMetadata['timestamp']
                    ];
                }
            }
        }

        if(!empty($draftPaths) && ($isDraftClean || $this->_config['pages.prioritize_manual_changes']))
        {
            foreach($draftPaths as $draftPath)
            {
                $this->_fs->delete($draftPath);
            }
        }

        $this->_updateMenuMetadata();

        // Set metadata
        $this->writeMetadata();
    }

    protected function _createDraftResource($path)
    {
        // Copy page/resource to draft dir
        $draftPath = $this->_draftBaseUrl() . $path;
        $this->_fs->copy($path, $draftPath);

        // Get file metadata
        $pageMetadata = $this->_fs->getMetadata($path);

        // This is a Page. Create draft and process it
        if (!$this->isResourcePath($path))
        {
            // Initialize Page
            $page = new Page($this->_fs->read($draftPath));

            if($page->isEditable())
            {
                // Normalize container names (add _cnt_ suffixes where no SC identification is set)
                $page->normalizeContainerNames();
            }

            // Prefix resource urls (prepend draft path for all resources urls and basedir path to relative paths)
            $page->prefixResourceUrls($this->_draftBaseUrl(), $this->getBase());

            // Update draft file content
            $this->_fs->update($draftPath, (string)$page);

            // Check for existing navigation in current page and store it if found
            if($hasNavigation = $this->hasMenu($page))
            {
                $this->_findMenus($page, $path);
            }

            if ($this->isPageFile($path))
            {
                $id = Utils::id();
                $this->_metadata['pages'][ $path ] = [
                    // Set page id
                    'id' => $id,
                    // Set page url
                    'url' => $this->pageFileUrl($path),
                    // Page menu order and menu text are set separately
                    'idx' => -1,
                    'navtitle' => '',
                    // Set page title
                    'title' => (string)$page->getPageTitle(),
                    // Set page description
                    'desc' => (string)$page->getPageDescription(),
                    // Set weather page has navigation or not
                    'navigation' => $hasNavigation
                ];
            }
        }

        $draftMetadata = $this->_fs->getMetadata($draftPath);

        // Remember last modification times
        $this->_metadata['files'][ $path ] = [
            $pageMetadata['timestamp'],
            $draftMetadata['timestamp']
        ];
    }

    /**
     * Updates menu metadata based on previously collected page files
     *
     * TODO : When menu manager is implemented this method will change (now checking only main menu)
     */
    protected function _updateMenuMetadata()
    {
        // Set menu order and menu item text for all page files in metadata
        if (isset($this->_metadata['menus']['main']) &&
            ($menuItems = $this->_metadata['menus']['main']['items']) &&
            !empty($this->_metadata['pages']))
        {
            foreach ($this->_metadata['pages'] as $path => &$properties)
            {
                foreach($menuItems as $no => $item)
                {
                    $url = $this->pageFilePath($item['url']);
                    $url = empty($url) ? $this->getDefaultIndex() : $url;
                    if ($this->_fs->has($url) && $this->_fs->get($url) instanceof Directory)
                    {
                        $url = $this->getDefaultIndex($url);
                    }

                    if ($path == $url)
                    {
                        $properties['idx'] = $no;
                        $properties['navtitle'] = $menuItems[ $no ][ 'text' ];
                        break;
                    }
                }
            }
        }
    }

    /**
     * Loads navigation sections found within passed page
     *
     * @param Page   $page
     * @param string $path
     */
    protected function _findMenus(Page $page, $path)
    {
        foreach($page->query('[class*="' . Menu::SC_MENU_BASE_CLASS . '"]') as $menu)
        {
            $menu = new Menu($menu);

            // TODO : until menu manager is implemented $name will always be 'main'
            $name = $menu->name();

            if(!isset($this->_metadata['menus'][$name]))
            {
                $this->_metadata['menus'][$name] = [
                    'pages' => [],
                    'items' => $menu->items()
                ];
            }

            if(array_search($path, $this->_metadata['menus'][$name]['pages']) === false)
            {
                array_push($this->_metadata['menus'][$name]['pages'], $path);
            }
        }
    }

    /**
     * Loads navigation sections found within passed page
     *
     * @param Page   $page
     * @param string $path

    protected function findMenus(Page $page, $path)
    {
    $menu = [];

    foreach($page->query($this->ctx['pages.nav.item_selector']) as $menuItem)
    {
    $element = $page->query($menuItem);
    $menu[] = [
    'text' => $element->text(),
    'url' => $element->attr('href')
    ];
    }

    if (!empty($menu))
    {
    if(array_search($path, $this->_metadata['menus']['pages']) === false)
    {
    array_push($this->_metadata['menus']['pages'], $path);
    }

    if (empty($this->_metadata['menus']['items']))
    {
    $this->_metadata['menus']['items'] = $menu;
    }
    }
    }*/

    public function hasMenu(Page $page)
    {
        return count($page->query('[class*="' . Menu::SC_MENU_BASE_CLASS . '"]')) > 0;
    }

    /**
     * Updates all menus in all pages with new menu content
     *
     * @param array $metadata Pages metadata
     */
    protected function _updateMenus($metadata)
    {
        // TODO : When menu manager is implemented this section will change (now checking only main menu)
        foreach($this->_metadata['menus']['main']['pages'] as $path)
        {
            $path = $this->_draftBaseUrl() . $path;

            $page = new Page($this->_fs->read($path));

            foreach($page->query('[class*="' . Menu::SC_MENU_BASE_CLASS . '"]') as $menu)
            {
                $menu = new Menu($menu);

                // TODO : until menu manager is implemented $name will always be 'main'
                $name = $menu->name();

                // Initialize menu items
                $items = [];
                // Read menu item details
                foreach($metadata as $item)
                {
                    if($item['idx'] != -1)
                    {
                        $items[$item['idx']] = [
                            'url' => $item['url'],
                            'text' => $item['navtitle']
                        ];
                    }
                }

                $menu->items($items);

                $page->findAndReplace(
                    Menu::SC_MENU_BASE_CLASS . ($name == 'main' ? '' : '-' . $name),
                    $menu->render($this->_config['pages.nav.item_template'], function($url) use ($path) {
                        return $this->pageFileUrl($path) == $url;
                    }, $this->_config['pages.nav.active_class'])
                );
            }

            $this->_fs->update($path, (string)$page);

            // Update last modified time in metadata
            $this->saveLastModified($path);
        }
    }

    protected function removeDraft()
    {
        $this->_fs->deletePaths($this->listScPaths($this->draftPath()));
        $this->_fs->delete($this->draftMarkerPath());
    }

    protected function newBackupContainerPath()
    {
        $path = $this->backupPath() . '/' . date('Y-m-d-H.i.s') . '-' . substr(uniqid(), -2);

        return $path;
    }

    /**
     * Remove all backups except for the last recent five.
     */
    protected function cleanupBackup()
    {
        $backups = $this->_fs->listContents($this->backupPath());
        usort($backups, function ($a, $b)
        {
            if ($a['timestamp'] < $b['timestamp'])
            {
                return -1;
            }
            else if ($a['timestamp'] == $b['timestamp'])
            {
                return 0;
            }
            else
            {
                return 1;
            }
        });
        $backups = array_reverse($backups);
        foreach ($backups as $idx => $backup)
        {
            if ($idx >= $this->_config['site.number_of_backups'])
            {
                $this->_fs->deleteDir($backup['path']);
            }
        }
    }

    public function backup()
    {
        $backupPath = $this->newBackupContainerPath();
        $this->_fs->createDir($backupPath);
        $this->_fs->createDir($backupPath . '/images');
        $this->_fs->createDir($backupPath . '/files');
        $this->_fs->copyPaths($this->listScPaths(), '', $backupPath);
        $this->cleanupBackup();
    }

    public function editSessionStart()
    {

    }

    protected function cleanupPublic()
    {
        $this->loadPageFilePaths();
        $pagePaths = $this->listScPagesPaths();
        foreach ($pagePaths as $pagePath)
        {
            $page = new Page($this->_fs->read($pagePath));

            if($page->isEditable())
            {
                // Remove dynamically added container names
                $page->cleanupContainerNames();
            }

            // Remove draft path prefix from resources and add relative prefix if page file or
            // webroot relative path prefix if include file
            // TODO: write comment why we are making difference here
            if($this->isPageFile($pagePath))
            {
                // For page files we need to add ../ to resource links if needed
                $page->unprefixResourceUrls($this->_draftBaseUrl(),
                    str_repeat('../', (count(explode('/', $pagePath)) - 1)));
            }
            else
            {
                $page->unprefixResourceUrls($this->_draftBaseUrl(), $this->getBase());
            }

            // Update source
            $this->_fs->update($pagePath, (string)$page);

            // Update last modified time in metadata
            $this->saveLastModified($pagePath);
        }
    }

    protected function cleanupDraft()
    {
        $draftResources = $this->draftResources();
        $allResources = $this->listScPaths($this->draftPath());
        foreach ($allResources as $resource)
        {
            if (!in_array($resource, $draftResources))
            {
                $this->_fs->delete($resource);
            }
        }
    }

    protected function draftResources()
    {
        $draftPagePaths = $this->listDraftPagePaths();
        $resources = array_merge([], $draftPagePaths);
        foreach ($draftPagePaths as $pagePath)
        {
            $page = new Page($this->_fs->read($pagePath), false);
            $resources = array_merge($resources, $page->listResourceUrls());
        }

        return array_unique($resources);
    }
}
