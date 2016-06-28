<?php

namespace Sitecake;

use \phpQuery;
use \DOMDocumentWrapper;
use \phpQueryObject;
use Sitecake\Exception\BadFormatException;
use Sitecake\Util\Beautifier;
use Sitecake\Util\HtmlUtils;
use Sitecake\Util\Utils;

class Page
{
    const SC_BASE_CLASS = 'sc-content';

    protected $_source;

    protected $_doc;

    protected $_containers;

    protected $_beautifier;

    protected function _createPhpQueryDocSafe($html)
    {
        $wrapper = new DOMDocumentWrapper($html, null, md5(mt_rand() . mt_rand()));
        phpQuery::$documents[ $wrapper->id ] = $wrapper;
        phpQuery::selectDocument($wrapper->id);

        return new phpQueryObject($wrapper->id);
    }

    public function __construct($html)
    {
        // Store page source
        $this->_source = $html;
        // Initialize page Document
        $this->_doc = $this->_createPhpQueryDocSafe($html);
        // Initialize HTML beautifier
        $this->_beautifier = new Beautifier();
    }

    public function __toString()
    {
        return $this->_source;
    }

    protected function _updateDoc()
    {
        $this->_doc->documentWrapper->load($this->_source);
    }

    protected function _evaluate($content)
    {
        ob_start();
        eval('?>' . $content);
        $result = ob_get_contents();
        ob_end_clean();

        return $result;
    }

    public function prefixResourceUrls($prefix, $base = '')
    {
        foreach (phpQuery::pq('a, img',	$this->_doc) as $node)
        {
            $attributes = ['src', 'href', 'srcset'];

            foreach($attributes as $attribute)
            {
                $value = $node->hasAttribute($attribute) ? $node->getAttribute($attribute) : false;
                if($value)
                {
                    // Strip basedir prefix for resource urls
                    if(!empty($base))
                    {
                        HtmlUtils::unprefixNodeAttr($node, $attribute, $base, function ($url) {
                            return Utils::isScResourceUrl($url);
                        });
                    }

                    // Add passed prefix to resource urls
                    HtmlUtils::prefixNodeAttr($node, $attribute, $prefix, function ($url) {
                        return Utils::isScResourceUrl($url);
                    });

                    $newValue = $node->getAttribute($attribute);

                    // Need to strip all '../' and duplicate / inside url
                    if(Utils::isScResourceUrl($newValue))
                    {
                        $newValue = str_replace(['../', './'], '', $newValue);
                        $newValue = str_replace(['//'], '/', $newValue);
                    }

                    if($value != $newValue)
                    {
                        $this->_source = preg_replace(
                            '/' . preg_quote($attribute . '="' . $value . '"', '/') . '/',
                            $attribute . '="' . $newValue . '"',
                            $this->_source, 1
                        );

                        $this->_updateDoc();
                    }
                }
            }
        }
    }

    public function unprefixResourceUrls($prefix, $base = '')
    {
        foreach (phpQuery::pq('a, img',	$this->_doc) as $node)
        {
            $attributes = ['src', 'href', 'srcset'];

            foreach($attributes as $attribute)
            {
                $attributeValue = $node->hasAttribute($attribute) ? $node->getAttribute($attribute) : false;
                if($attributeValue)
                {
                    // Strip passed prefix from resource urls
                    HtmlUtils::unprefixNodeAttr($node, $attribute, $prefix, function ($url) {
                        return Utils::isScResourceUrl($url);
                    });

                    $newValue = $node->getAttribute($attribute);

                    // Prepend $base if passed
                    if(!empty($base))
                    {
                        if(empty($newValue))
                        {
                            $newValue = $base;
                        }
                        else
                        {
                            // Add relative url prefix to resource urls
                            HtmlUtils::prefixNodeAttr($node, $attribute, $base, function ($url) use ($base) {
                                return Utils::isScResourceUrl($url) && strpos($url, $base) !== 0;
                            });

                            $newValue = $node->getAttribute($attribute);
                        }
                    }

                    // Need to strip duplicate / inside url
                    if(Utils::isScResourceUrl($newValue))
                    {
                        $newValue = str_replace(['//'], '/', $newValue);
                    }

                    if($attributeValue != $newValue)
                    {
                        $this->_source = preg_replace(
                            '/' . preg_quote($attribute . '="' . $attributeValue . '"', '/') . '/',
                            $attribute . '="' . $newValue . '"',
                            $this->_source, 1
                        );

                        $this->_updateDoc();
                    }
                }
            }
        }
    }

    public function listResourceUrls($filter = null)
    {
        $urls = array();
        foreach ($this->containerNodes() as $container)
        {
            if(is_callable($filter))
            {
                if($filter($container))
                {
                    $urls = array_merge($urls, $this->_listContainerResourceUrls($container));
                }
            }
            else
            {
                $urls = array_merge($urls, $this->_listContainerResourceUrls($container));
            }
        }

        return $urls;
    }

    public function updateResourcePath($oldPath, $newPath)
    {
        $this->_source = preg_replace(
            '/' . preg_quote($oldPath, '/') . '/',
            $newPath,
            $this->_source
        );

        $this->_updateDoc();
    }

    /**
     * Returns array of elements defined by passed selector
     *
     * @param string $selector
     *
     * @return false|phpQueryObject|\QueryTemplatesParse|\QueryTemplatesPhpQuery|\QueryTemplatesSource|\QueryTemplatesSourceQuery
     * @throws \Exception
     */
    public function query($selector)
    {
        return phpQuery::pq($selector, $this->_doc);
    }

    /**
     * Returns the page title (the title tag).
     *
     * @return string the current value of the title tag
     */
    public function getPageTitle()
    {
        return phpQuery::pq('title', $this->_doc)->html();
    }

    /**
     * Sets the page title (the title tag).
     *
     * @param string $val Title to be set
     */
    public function setPageTitle($val)
    {
        if ($val === '')
        {
            // If empty value passed we need to remove title tag
            $this->_source = preg_replace('/[ \t]*<title>(.*)<\/title>\s*/', '', $this->_source);
        }
        else
        {
            $title = phpQuery::pq('title', $this->_doc);
            if ($title->count() > 0)
            {
                $this->_source = str_replace((string)$title, sprintf('<title>%s</title>', $val), $this->_source);
            }
            else
            {
                if ($inserted = HtmlUtils::insertInto($this->_source, 'head', sprintf('<title>%s</title>', $val)))
                {
                    $this->_source = $inserted;
                }
            }
        }

        $this->_updateDoc();
    }

    /**
     * Reads the page description meta tag.
     *
     * @return string       current description text
     */
    public function getPageDescription()
    {
        $text = '';
        $tag = phpQuery::pq('meta[name="description"]', $this->_doc);
        if ($tag->count() > 0)
        {
            $text = phpQuery::pq($tag->elements[0])->attr('content');
        }

        return $text;
    }

    /**
     * Sets the page description meta tag with the given content.
     *
     * @param string $text Description to be set
     */
    public function setPageDescription($text)
    {
        if ($text === '')
        {
            // If text is empty we need to remove meta description tag
            $this->_source = preg_replace('/[ \t]*<meta.+(name[^=]*=[^"\']*(\'|")description(\'|"))[^>]*>\s*/', '',
                $this->_source);
        }
        else
        {
            $meta = sprintf('<meta name="description" content="%s">', $text);

            // Try to find and replace current meta description tag
            $metaDesc = phpQuery::pq('meta[name="description"]', $this->_doc);
            if ($metaDesc->count() > 0)
            {
                $this->_source = preg_replace('/<meta.+(name[^=]*=[^"\']*(\'|")description(\'|"))[^>]*>/',
                    $meta, $this->_source);
            }
            else
            {
                // Try to insert meta description tag into head
                if($inserted = HtmlUtils::insertInto($this->_source, 'head', $meta))
                {
                    $this->_source = $inserted;
                }
                else
                {
                    // No head tags present. Try to find title tag and insert meta description after it
                    if($inserted = HtmlUtils::insertAfter($this->_source, 'title', $meta))
                    {
                        $this->_source = $inserted;
                    }
                }
            }
        }

        $this->_updateDoc();
    }

    /**
     * Returns details for specific container based on passed container name and it's position inside page.
     * Returned array ic containing next details :
     *      + whitespace - whitespace before specific container inside that row
     *      + openingTag - container opening tag
     *      + tagName - container's tag name
     *      + positions - start and end position of specific container inside the file
     *
     * @param string $selector Container selector
     * @param int $position   Order number of appearance of specific container inside the file
     *                        (There can be more than one container with the same name)
     *
     * @return array
     */
    protected function _containerDetails($selector, $position)
    {
        //$found = Utils::match('/([ \t]*)(<(?:"[^"]*"[\'"]*|\'[^\']*\'[\'"]*|[^\'">])+>)/', $this->_source, $matches, PREG_OFFSET_CAPTURE);
        // To find tag name  ad brackets around \w+
        $found = Utils::match('/([ \t]*)(<\/?\w+(?:(?:\s+\w+\s*(?:=\s*(?:".*?"|\'.*?\'|[\^\'">\s]+)?)?)+\s*|\s*)\/?>)/',
            $this->_source, $matches, PREG_OFFSET_CAPTURE);
        $return = [];
        if(!empty($found) && !empty($matches[2]))
        {
            $positionCounter = 0;
            foreach($matches[2] as $no => $element)
            {
                if (preg_match('/<([^\s]+).*("|\s)' . preg_quote($selector) . '(\s|"|\')[^>]*>/', $element[0], $m))
                {
                    if($positionCounter < $position)
                    {
                        $positionCounter++;
                        continue;
                    }

                    $tag = $m[1];
                    $return['whitespace'] = $matches[1][$no][0];
                    $return['openingTag'] = $m[0];
                    $return['tagName'] = $tag;
                    $return['positions'] = [$element[1]];
                    $innerElementCount = 0;

                    for($i = ($no+1); $i < count($matches[2]); $i++)
                    {
                        $el = $matches[2][$i];
                        if(preg_match('/<' . preg_quote($tag) . '/', $el[0]))
                        {
                            $innerElementCount++;
                        }

                        if(preg_match('/<\/' . preg_quote($tag) . '>/', $el[0]))
                        {
                            if($innerElementCount)
                            {
                                $innerElementCount--;
                            }
                            else
                            {
                                $return['positions'][] = $el[1];
                                break 2;
                            }
                        }
                    }

                    throw new BadFormatException([
                        'name' => $selector
                    ]);
                }
            }
        }
        return $return;
    }

    /**
     * Sets container content (beautified)
     *
     * @param string $containerName Container name
     * @param string $content Content to set into container
     *
     * @throws \Exception
     */
    public function setContainerContent($containerName, $content)
    {
        $this->findAndReplace(self::SC_BASE_CLASS . '-' . $containerName, $content);
    }

    public function findAndReplace($selector, $content)
    {
        foreach (phpQuery::pq('.' . $selector, $this->_doc) as $no => $node)
        {
            $containerDetails = $this->_containerDetails($selector, $no);
            $updated = $containerDetails['openingTag'] . "\n" .
                       $this->_beautifier->indent($content, $containerDetails['whitespace']) .
                       $containerDetails['whitespace'];

            $this->_source = mb_substr($this->_source, 0, $containerDetails['positions'][0]) . $updated .
                             mb_substr($this->_source, $containerDetails['positions'][1]);

            $this->_updateDoc();
        }
    }

    /**
     * Returns resource URL's (files and images) for specified container
     *
     * @param string $container Container name
     *
     * @return array
     * @throws \Exception
     */
    protected function _listContainerResourceUrls($container)
    {
        $urls = [];
        $html = (string)phpQuery::pq($container, $this->_doc);
        preg_match_all("/[^\\s\"',]*(?:files|images)\\/[^\\s]*\\-sc[0-9a-f]{13}[^\.]*\\.[0-9a-zA-Z]+/",
            $html, $matches);
        foreach ($matches[0] as $match)
        {
            if (Utils::isScResourceUrl($match))
            {
                array_push($urls, urldecode($match));
            }
        }

        return $urls;
    }

    protected function containerNodes()
    {
        $containers = array();
        foreach (phpQuery::pq('[class*="' . self::SC_BASE_CLASS . '"]', $this->_doc) as $node)
        {
            $container = phpQuery::pq($node, $this->_doc);
            $class = $container->attr('class');
            if (preg_match('/(^|\s)' . preg_quote(self::SC_BASE_CLASS) . '(\-[^\s]+)*(\s|$)/', $class, $matches))
            {
                array_push($containers, $container);
            }
        }

        return $containers;
    }

    /**
     * Returns weather page is editable (does it contains .sc-content containers)
     *
     * @return bool
     */
    public function isEditable()
    {
        return count($this->query('[class*="' . self::SC_BASE_CLASS . '"]')) > 0;
    }

    /**
     * Turns all the unnamed containers (having just .sc-content class specified) in the page to named containers.
     * Method adds sc-content-<code>'-_cnt_' . mt_rand() . mt_rand()</code> class to each container that is not named
     * @throws \Exception
     */
    public function normalizeContainerNames()
    {
        $found = Utils::match('/(?:\sclass\=\s*[\"\'])(.+?(?=\"|\').*?)(?:[\"\'])/',
            $this->_source, $matches, PREG_OFFSET_CAPTURE);

        if(!empty($found) && !empty($matches[1]))
        {
            $offset = 0;
            foreach($matches[1] as $match)
            {
                if(preg_match('/(^|\s)' . preg_quote(self::SC_BASE_CLASS) . '($|\-|\s)/', $match[0]) &&
                   !preg_match('/(^|\s)' . preg_quote(self::SC_BASE_CLASS) . '\-[^\s]+($|\s)/', $match[0]))
                {
                    $generatedClass = self::SC_BASE_CLASS . '-_cnt_' . mt_rand() . mt_rand();
                    $class = $match[0] . ' ' . $generatedClass;
                    $beforeClassString = mb_substr($this->_source, 0, ($match[1] + $offset));
                    $afterClassPosition = mb_strlen($beforeClassString) + mb_strlen($match[0]);
                    $this->_source = $beforeClassString . $class .
                                     mb_substr($this->_source, $afterClassPosition);
                    $offset += mb_strlen(' ' . $generatedClass);
                }
            }

            $this->_updateDoc();
        }
    }

    public function cleanupContainerNames()
    {
        foreach ($this->containerNodes() as $node)
        {
            $container = phpQuery::pq($node, $this->_doc);
            $class = $container->attr('class');
            if (preg_match('/(^|\s)(' . preg_quote(self::SC_BASE_CLASS) . '\-_cnt_[0-9]+)/', $class, $matches))
            {
                $this->_source = preg_replace(
                    '/' . preg_quote($matches[0]) . '/',
                    '',
                    $this->_source
                );

                $this->_updateDoc();
            }
        }
    }

    public function isUnnamedContainer($container)
    {
        $class = $container->attr('class');
        return (bool)preg_match('/(^|\s)(' . preg_quote(self::SC_BASE_CLASS) . '\-_cnt_[0-9]+)($|\s)/', $class);
    }

    /**
     * Returns a list of container names.
     *
     * @return array a list of container names
     */
    public function containers()
    {
        if (!$this->_containers)
        {
            $this->_containers = [];
            foreach ($this->containerNodes() as $container)
            {
                preg_match('/(^|\s)' . preg_quote(self::SC_BASE_CLASS) . '-([^\s]+)/', $container->attr('class'), $matches);
                if (isset($matches[2]))
                {
                    if(array_search($matches[2], $this->_containers) === false)
                    {
                        array_push($this->_containers, $matches[2]);
                    }
                }
            }
        }

        return $this->_containers;
    }
}