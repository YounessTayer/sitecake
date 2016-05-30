<?php

namespace Sitecake;

use Sitecake\Util\HtmlUtils;

class Menu
{
    const SC_MENU_BASE_CLASS = 'sc-nav';

    private $__node;

    /**
     * At the moment all menus are treated as main. When menu manager is implemented this will deffer from menu to menu
     * @var string
     */
    protected $_name = 'main';

    protected $_items;

    public function __construct(\DOMElement $node)
    {
        $this->__node = $node;

        $this->_init();
    }

    protected function _init()
    {
        $class = $this->__node->getAttribute('class');

        if(preg_match('/(^|\s)(' . preg_quote(self::SC_MENU_BASE_CLASS) . '(\-([^\s]+))*)(\s|$)/', $class, $matches))
        {
            // TODO : Uncomment this when menu manager is implemented in editor
            //if($matches[3] != self::SC_MENU_BASE_CLASS && isset($matches[5]))
            //{
            //    $this->_name = $matches[5];
            //}

            $this->_findItems();
        }
    }

    protected function _findItems()
    {
        $doc = new \DOMDocument();

        // Suppress HTML5 errors
        libxml_use_internal_errors(true);

        $doc->loadHTML((string)$this);
        
        libxml_use_internal_errors(false);

        foreach($doc->getElementsByTagName('a') as $no => $menuItem)
        {
            $this->_items[] = [
                'text' => $menuItem->textContent,
                'url' => $menuItem->getAttribute('href')
            ];
        }
    }

    public function render($template, $isActive = null, $activeClass = '')
    {
        $this->__node->nodeValue = '';
        $menuItems = '';

        foreach($this->_items as $no => $item)
        {
            $itemHTML = str_replace('${url}', $item['url'], $template);
            $itemHTML = str_replace('${title}', $item['text'], $itemHTML);
            $itemHTML = str_replace('${order}', $no, $itemHTML);

            if(strpos($itemHTML, '${active}') !== false)
            {
                if(is_callable($isActive) && $isActive($item['url']))
                {
                    $itemHTML = str_replace('${active}', $activeClass, $itemHTML);
                }
                else
                {
                    $itemHTML = str_replace('${active}', '', $itemHTML);
                }
            }

            $menuItems .= $itemHTML;
            HtmlUtils::appendHTML($this->__node, $itemHTML);
        }

        return $menuItems;//(string)$this;
    }

    /**
     * @return string
     */
    public function __toString()
    {
        return trim($this->__node->ownerDocument->saveHTML($this->__node));
    }

    public function name()
    {
        return $this->_name;
    }

    public function items($items = null)
    {
        if(empty($items))
        {
            return $this->_items;
        }

        $this->_items = $items;
    }
}