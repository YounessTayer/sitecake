<?php

namespace Sitecake;

use \phpQuery;

class PageTest extends \PHPUnit_Framework_TestCase
{

    function test_prefixResourceUrls()
    {
        $html = '<html><head></head><body>' .
                '<div>' .
                '<a id="a1" href="a1">a1</a>' .
                '</div>' .
                '<div class="sc-content-1">' .
                '<a id="a2" href="a2">a2</a>' .
                '</div>' .
                '<div class="sc-content">' .
                '<a id="a3" href="files/a3-sc123456789abcd.doc">a3</a>' .
                '<img id="img1" src="images/img1-sc123456789abcd-00.jpg" srcset="draft/images/img1-sc123456789abcd-00.jpg 1, img2.jpg 2"/>' .
                '</div>' .
                '<div class="sc-content sc-content-2">' .
                '<a id="a4" href="javascript:files/a4-sc123456789abcd.doc">a4</a>' .
                '<a id="a5" href="#images/a5-sc123456789abcd.doc">a5</a>' .
                '<a id="a6" href="https://files/a6-sc123456789abcd.doc">a6</a>' .
                '</div>' .
                '</body></html>';

        $page = new Page($html);

        $page->prefixResourceUrls('p/');

        $doc = phpQuery::newDocument((string)$page);

        $this->assertEquals('a1', phpQuery::pq('#a1', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('a2', phpQuery::pq('#a2', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('p/files/a3-sc123456789abcd.doc',
            phpQuery::pq('#a3', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('javascript:files/a4-sc123456789abcd.doc',
            phpQuery::pq('#a4', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('#images/a5-sc123456789abcd.doc',
            phpQuery::pq('#a5', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('https://files/a6-sc123456789abcd.doc',
            phpQuery::pq('#a6', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('p/images/img1-sc123456789abcd-00.jpg',
            phpQuery::pq('#img1', $doc)->elements[0]->getAttribute('src'));
        $this->assertEquals('p/draft/images/img1-sc123456789abcd-00.jpg 1, img2.jpg 2',
            phpQuery::pq('#img1', $doc)->elements[0]->getAttribute('srcset'));
    }

    function testUnprefixResourceUrls()
    {
        $html = '<html><head></head><body>' .
                '<div>' .
                '<a id="a1" href="p/a1">a1</a>' .
                '</div>' .
                '<div class="sc-content-1">' .
                '<a id="a2" href="p/a2">a2</a>' .
                '</div>' .
                '<div class="sc-content">' .
                '<a id="a3" href="p/files/a3-sc123456789abcd.doc">a3</a>' .
                '<img id="img1" src="p/images/img1-sc123456789abcd-00.jpg" srcset="draft/images/img1-sc123456789abcd-00.jpg 1, p/img2.jpg 2"/>' .
                '</div>' .
                '<div class="sc-content sc-content-2">' .
                '<a id="a4" href="javascript:p/files/a4-sc123456789abcd.doc">a4</a>' .
                '<a id="a5" href="#p/images/a5-sc123456789abcd.doc">a5</a>' .
                '<a id="a6" href="https://p/files/a6-sc123456789abcd.doc">a6</a>' .
                '</div>' .
                '</body></html>';

        $page = new Page($html);
        $page->unprefixResourceUrls('p/', '/');

        $doc = phpQuery::newDocument((string)$page);

        $this->assertEquals('p/a1', phpQuery::pq('#a1', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('p/a2', phpQuery::pq('#a2', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('/files/a3-sc123456789abcd.doc',
            phpQuery::pq('#a3', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('javascript:p/files/a4-sc123456789abcd.doc',
            phpQuery::pq('#a4', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('#p/images/a5-sc123456789abcd.doc',
            phpQuery::pq('#a5', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('https://p/files/a6-sc123456789abcd.doc',
            phpQuery::pq('#a6', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('/images/img1-sc123456789abcd-00.jpg',
            phpQuery::pq('#img1', $doc)->elements[0]->getAttribute('src'));
        $this->assertEquals('/draft/images/img1-sc123456789abcd-00.jpg 1, p/img2.jpg 2',
            phpQuery::pq('#img1', $doc)->elements[0]->getAttribute('srcset'));

        $page = new Page($html);
        $page->unprefixResourceUrls('p/', '../');

        $doc = phpQuery::newDocument((string)$page);

        $this->assertEquals('../files/a3-sc123456789abcd.doc',
            phpQuery::pq('#a3', $doc)->elements[0]->getAttribute('href'));
        $this->assertEquals('../images/img1-sc123456789abcd-00.jpg',
            phpQuery::pq('#img1', $doc)->elements[0]->getAttribute('src'));
    }

    function test_listResourceUrls()
    {
        $html = '<html><head></head><body>' .
                '<div>' .
                '<a id="a1" href="p/a1">a1</a>' .
                '</div>' .
                '<div class="sc-content-1">' .
                '<a id="a2" href="p/a2">a2</a>' .
                '</div>' .
                '<div class="sc-content">' .
                '<a id="a3" href="p/files/a3-sc123456789abcd.doc">a3</a>' .
                '<img id="img1" src="p/images/img1-sc123456789abcd-00.jpg" srcset="draft/images/img1-sc123456789abcd-00.jpg 451w,draft/images/img2-sc123456789abcd-00.jpg 300w, p/img2.jpg 200w"/>' .
                '</div>' .
                '<div class="sc-content sc-content-2">' .
                '<a id="a4" href="javascript:p/files/a4-sc123456789abcd.doc">a4</a>' .
                '<a id="a5" href="#p/images/a5-sc123456789abcd.doc">a5</a>' .
                '<a id="a6" href="https://p/files/a6-sc123456789abcd.doc">a6</a>' .
                '</div>' .
                '</body></html>';

        $page = new Page($html);

        $urls = $page->listResourceUrls();

        $this->assertEquals(4, count($urls));
        $this->assertEquals('p/files/a3-sc123456789abcd.doc', $urls[0]);
        $this->assertEquals('p/images/img1-sc123456789abcd-00.jpg', $urls[1]);
        $this->assertEquals('draft/images/img1-sc123456789abcd-00.jpg', $urls[2]);
        $this->assertEquals('draft/images/img2-sc123456789abcd-00.jpg', $urls[3]);

    }

    function testToString()
    {
        $html = '<html><head>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $o = $page->__toString();

        $this->assertTrue(is_string($o));
        $this->assertEquals($html, (string)$page);
    }

    function testSetContainerContent()
    {
        $html = '<html><head>' .
                '<title>page title</title>' .
                '</head><body>' .
                '<div class="block sc-content-cnt1"></div>' .
                '</body></html>';

        $page = new Page($html);

        $page->setContainerContent('cnt1', '<h1>Heading</h1><p>test content</p>');

        $expected =
            '<html>
	<head>
	    <title>page title</title>
	</head>
	<body>
		<div class="block sc-content-cnt1">
			<h1>Heading</h1>
			<p>test content</p>
		</div>
	</body>
</html>';

        $this->assertXmlStringEqualsXmlString($expected, (string)$page);
    }

    function testContainers()
    {
        $html = '<html><head>' .
                '</head><body>' .
                '<div class="block sc-content-cnt1"></div>' .
                '<div class=" sc-content-cnt2 "></div>' .
                '<div class="sc-content-cnt3"></div>' .
                '<div class="blocksc-content-cnt4"></div>' .
                '<div class="blocksc-content- cnt5"></div>' .
                '<div class="sc-content"></div>' .
                '</body></html>';

        $page = new Page($html);
        $cnts = $page->containers();

        $this->assertTrue(is_array($cnts));
        $this->assertEquals(3, count($cnts));
        $this->assertTrue(in_array('cnt1', $cnts));
        $this->assertTrue(in_array('cnt2', $cnts));
        $this->assertTrue(in_array('cnt3', $cnts));
    }

    function testNormalizeContainerNames()
    {
        $html = '<html><head>' .
                '</head><body>' .
                '<a class="no-sc-content"></a>' .
                '<nav class="sc-nav"></nav>' .
                '<article class="col-lg-10"></article>' .
                '<div class="sc-content-cnt1"></div>' .
                '<div class="before sc-content-cnt2"></div>' .
                '<div class="sc-content-cnt3 after"></div>' .
                '<div class="before sc-content-cnt4 after"></div>' .
                '<div class="sc-content"></div>' .
                '<div class="before sc-content"></div>' .
                '<div class="sc-content after"></div>' .
                '<div class="before sc-content after"></div>' .
                '<div class="sc-content sc-content"></div>' .
                '<div class="sc-content sc-content-ctn5"></div>' .
                '<div class="sc-content-ctn6 sc-content"></div>' .
                '<div class="sc-content-"></div>' .
                '</body></html>';

        $page = new Page($html);
        $page->normalizeContainerNames();
        $cnts = $page->containers();

        $this->assertTrue(is_array($cnts));
        $this->assertEquals(12, count($cnts));
        $this->assertTrue(in_array('cnt1', $cnts));
        $this->assertEquals(0, strpos($cnts[1], '_cnt_'));
    }

    function testCleanupContainerNames()
    {
        $html = '<html><head>' .
                '</head><body>' .
                '<div class="sc-content-cnt1"></div>' .
                '<div class="sc-content sc-content-_cnt_12345"></div>' .
                '<div class="test sc-content-_cnt_abcd some"></div>' .
                '<div class="sc-content-"></div>' .
                '</body></html>';

        $page = new Page($html);
        $page->cleanupContainerNames();
        $cnts = $page->containers();

        $this->assertTrue(is_array($cnts));
        $this->assertEquals(2, count($cnts));
        $this->assertTrue(in_array('cnt1', $cnts));
        $this->assertFalse(strpos((string)$page, 'sc-content sc-content'));
    }

    function testGetPageTitle()
    {
        $html = '<html><head>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $this->assertEquals('', $page->getPageTitle());

        $html = '<html><head>' .
                '<title>page title</title>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $this->assertEquals('page title', $page->getPageTitle());
    }

    function testSetPageTitle()
    {
        /**
         * Test adding title tag to page containing head
         */
        $html = '<html><head>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageTitle('some title');
        phpQuery::newDocument((string)$page);
        $this->assertEquals('some title', phpQuery::pq('title')->html());

        /**
         * Test adding title tag to page containing only opening head tag
         */
        $html = '<html><head>' .
                '<?php echo "</head>"; ?>' .
                '<body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageTitle('some title');
        phpQuery::newDocument((string)$page);
        $this->assertEquals('some title', phpQuery::pq('title')->html());

        /**
         * Test adding title to page containing only closing head tag
         */
        $html = '<html>' .
                '<?php echo "<head>"; ?>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageTitle('some title');
        phpQuery::newDocument((string)$page);
        $this->assertEquals('some title', phpQuery::pq('title')->html());

        /**
         * Test removing title tag from page
         */
        $html = '<html><head>' .
                '<title>some title</title>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageTitle('');
        phpQuery::newDocument((string)$page);
        $this->assertEquals(0, phpQuery::pq('title')->count());
    }

    function testGetPageDescription()
    {
        $html = '<html><head>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $this->assertEquals('', $page->getPageDescription());

        $html = '<html><head>' .
                '<meta name="description" content="page desc">' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $this->assertEquals('page desc', $page->getPageDescription());
    }

    function testSetPageDescription()
    {
        /**
         * Test adding meta description to page containing head
         */
        $html = '<html><head>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageDescription('page page');
        phpQuery::newDocument((string)$page);
        $this->assertEquals('page page',
            phpQuery::pq('meta[name="description"]')->elements[0]->getAttribute('content'));

        /**
         * Test adding meta description to page containing only opening head tag
         */
        $html = '<html><head>' .
                '<?php echo "</head>"; ?>' .
                '<body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageDescription('page page');
        phpQuery::newDocument((string)$page);
        $this->assertEquals('page page',
            phpQuery::pq('meta[name="description"]')->elements[0]->getAttribute('content'));

        /**
         * Test adding meta description to page containing only closing head tag
         */
        $html = '<html>' .
                '<?php echo "<head>"; ?>' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageDescription('page page');
        phpQuery::newDocument((string)$page);
        $this->assertEquals('page page',
            phpQuery::pq('meta[name="description"]')->elements[0]->getAttribute('content'));

        /**
         * Test adding meta description to page without head tag but with title tag present
         */
        $html = '<html>' .
                '<?php echo "<head>"; ?>' .
                '<title>Some title</title>' .
                '<?php echo "</head>"; ?>' .
                '<body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageDescription('page page');
        phpQuery::newDocument((string)$page);
        $this->assertEquals('page page',
            phpQuery::pq('meta[name="description"]')->elements[0]->getAttribute('content'));

        /**
         * Test removing meta description from page
         */
        $html = '<html><head>' .
                '<meta name="description" content="page desc">' .
                '</head><body>' .
                '</body></html>';

        $page = new Page($html);
        $page->setPageDescription('');
        phpQuery::newDocument((string)$page);
        $this->assertEquals(0, phpQuery::pq('meta[name="description"]')->count());
    }
}