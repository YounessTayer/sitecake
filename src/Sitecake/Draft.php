<?php

namespace Sitecake;

use \phpQuery;
use Sitecake\Util\HtmlUtils;
use Sitecake\Util\Utils;

class Draft extends Page
{
    protected $_evaluated;

    public function __construct($html)
    {
        parent::__construct($html);
        $this->_evaluated = $this->_createPhpQueryDocSafe($this->_evaluate($html));
    }

    /**
     * Adds data-pageid attribute to sitecake meta tag
     * Needed for SC editor to work properly.
     *
     * @param int $id ID to set
     *
     * @return void
     */
    public function setPageId($id)
    {
        if ('' == $this->getMetadataAttr('pageid'))
        {
            $this->addMetadataAttr('pageid', $id);
        }
    }

    /**
     * Adds the 'noindex, nofollow' meta tag in the draft header, if not present.
     */
    public function addRobotsNoIndexNoFollow()
    {
        if (phpQuery::pq('meta[content="noindex, nofollow"]', $this->_evaluated)->count() === 0)
        {
            phpQuery::pq('head', $this->_evaluated)->prepend('<meta name="robots" content="noindex, nofollow">');
        }
    }

    public function render($entryPointPath)
    {
        $this->_adjustNavLinks($entryPointPath);

        return (string)$this->_evaluated;
    }

    public function normalizeResourcePaths($base)
    {
        foreach (phpQuery::pq('a, img', $this->_evaluated) as $node)
        {
            $attributes = ['src', 'href', 'srcset'];

            foreach ($attributes as $attribute)
            {
                $value = $node->hasAttribute($attribute) ? $node->getAttribute($attribute) : false;

                if ($value)
                {
                    // Add basedir prefix to all relative urls that are not resource urls if it's not already there
                    HtmlUtils::prefixNodeAttr($node, $attribute, $base, function ($url) use ($base)
                    {
                        return Utils::isResourceUrl($url) && !Utils::isScResourceUrl($url) && strpos($url, $base) !== 0;
                    });

                    $newValue = $node->getAttribute($attribute);

                    // Need to strip all '../' and duplicate / inside url
                    if (Utils::isResourceUrl($newValue) && !Utils::isScResourceUrl($newValue))
                    {
                        $newValue = str_replace(['../', './'], '', $newValue);
                        $newValue = str_replace(['//'], '/', $newValue);
                    }

                    $node->setAttribute($attribute, $newValue);
                }
            }
        }
    }

    protected function _adjustNavLinks($entryPointPath)
    {
        foreach (phpQuery::pq('.sc-nav a', $this->_evaluated) as $node)
        {
            $href = $node->getAttribute('href');
            if (!Utils::isExternalNavLink($href) && Utils::isResourceUrl($href) && !Utils::isScResourceUrl($href))
            {
                $node->setAttribute('href', $entryPointPath . '?page=' . ltrim($href, './'));
            }
        }
    }

    public function appendCodeToHead($code)
    {
        HtmlUtils::appendToHead($this->_evaluated, $code);
    }

    public function addMetadata()
    {
        if (phpQuery::pq('meta[content="sitecake"]', $this->_evaluated)->count() === 0)
        {
            phpQuery::pq('head', $this->_evaluated)->prepend('<meta name="application-name" content="sitecake"/>');
        }
    }

    public function removeMetadata()
    {
        phpQuery::pq('meta[content="sitecake"]', $this->_evaluated)->remove();
    }

    /**
     * Adds an attribute to the sitecake metadata tag. If the metadata tag does not
     * exists it will be created.
     *
     * @param string $attr  attribute name
     * @param string $value attribute value
     */
    public function addMetadataAttr($attr, $value)
    {
        $this->addMetadata();
        phpQuery::pq('meta[content="sitecake"]', $this->_evaluated)->attr('data-' . $attr, $value);
    }

    /**
     * Removes the specified attribute of the sitecake meta tag.
     *
     * @param  string $attr attribute name
     */
    public function removeMetadataAttr($attr)
    {
        phpQuery::pq('meta[content="sitecake"]', $this->_evaluated)->removeAttr('data-' . $attr);
    }

    /**
     * Reads the metadata attribute value.
     *
     * @param  string $attr attribute name
     *
     * @return string       returns the attribute value or an empty string if attribute is not present
     */
    public function getMetadataAttr($attr)
    {
        return phpQuery::pq('meta[content="sitecake"]', $this->_evaluated)->attr('data-' . $attr);
    }
}