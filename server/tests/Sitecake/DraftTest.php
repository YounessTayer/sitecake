<?php

namespace Sitecake;

use \phpQuery;
use Sitecake\Util\Utils;

class DraftTest extends \PHPUnit_Framework_TestCase
{
    public function testSetPageId()
    {
        /**
         * Adding page ID when there is no application-name=sitecake meta tag
         */
        $html = '<html><head>'.
                '</head><body>'.
                '</body></html>';

        $page = new Draft($html);
        $page->setPageId(Utils::id());
        phpQuery::newDocument($page->render('sitecake.php'));
        $this->assertEquals(1, phpQuery::pq("meta[content='sitecake']")->count());
        $this->assertEquals(1, preg_match('/.+/', phpQuery::pq("meta[content='sitecake']")->get(0)->getAttribute('data-pageid')));

        /**
         * Adding page ID when application-name=sitecake meta tag is present
         */
        $html = '<html><head>'.
                '<meta name="application-name" content="sitecake"/>'.
                '</head><body>'.
                '</body></html>';

        $page = new Draft($html);
        $page->setPageId(Utils::id());
        phpQuery::newDocument($page->render('sitecake.php'));
        $this->assertEquals(1, phpQuery::pq('meta[data-pageid]')->count());
        $this->assertEquals(1, preg_match('/.+/', phpQuery::pq('meta[data-pageid]')->get(0)->getAttribute('data-pageid')));

    }

    public function testAddRobotsNoIndexNoFollow()
    {
        /**
         * Test adding meta robots to draft containing head
         */
        $html = '<html><head>' .
                '</head><body>' .
                '</body></html>';

        $draft = new Draft($html);
        $draft->addRobotsNoIndexNoFollow();
        phpQuery::newDocument($draft->render('sitecake.php'));
        $this->assertEquals('noindex, nofollow',
            phpQuery::pq('meta[name="robots"]')->elements[0]->getAttribute('content'));
    }

    public function testRender()
    {
        // Needed for Utils::isExternalNavLink method
        $_SERVER['HTTP_HOST'] = "test.sitecake.com";

        // Test evaluation with php code present
        $html = '<html><head>' .
                '</head><body>' .
                '<?php echo "<div class=\"c1 sc-content\"></div>"; ?>' .
                '</body></html>';

        $page = new Draft($html);

        phpQuery::newDocument($page->render('sitecake.php'));

        $expected = new \DOMDocument();
        $expected->loadXML('<div class="c1 sc-content"></div>');

        $this->assertEqualXMLStructure($expected->firstChild, phpQuery::pq('body')->elements[0]->firstChild);

        // Test evaluation without php code present
        $html = '<html><head>' .
                '</head><body>' .
                '<div class="c1 sc-content"></div>' .
                '</body></html>';

        $page = new Draft($html);

        phpQuery::newDocument($page->render('sitecake.php'));

        $expected = new \DOMDocument();
        $expected->loadXML('<div class="c1 sc-content"></div>');

        $this->assertEqualXMLStructure($expected->firstChild, phpQuery::pq('body')->elements[0]->firstChild);

        // Test evaluation with navigation links
        $html = '<html><head>' .
                '</head><body>' .
                    '<div class="sc-nav">' .
                    '<a id="link1" href="page1.html">first link</a>' .
                '<span><a id="link2" href="page2.php">second link</a></span>' .
                '</div>' .
                '<div class="sc-content"><a id="link3" href="page3.html">editable link</a></div>' .
                '<div><a id="link4" href="page4.html">non editable link</a></div>' .
                '</body></html>';

        $page = new Draft($html);

        phpQuery::newDocument($page->render('sitecake.php'));

        $this->assertEquals('sitecake.php?page=page1.html', phpQuery::pq('#link1')->attr('href'));
        $this->assertEquals('sitecake.php?page=page2.php', phpQuery::pq('#link2')->attr('href'));
        $this->assertEquals('page3.html', phpQuery::pq('#link3')->attr('href'));
        $this->assertEquals('sitecake.php?page=page4.html', phpQuery::pq('#link4')->attr('href'));
    }

    public function testNormalizeResourcePaths()
    {

    }

    public function test_adjustNavLinks()
    {
        // Needed for Utils::isExternalNavLink method
        $_SERVER['HTTP_HOST'] = "test.sitecake.com";

        $html = '<html><head>'.
                '</head><body>'.
                '<ul class="sc-nav">'.
                '<li><a id="l1" href="about.html">About</a></li>'.
                '<li><a id="l2" href="/doc.html">Doc</a></li>'.
                '<li><a id="l3" href="http://google.com"></a></li>'.
                '<li><a id="l6" href="/about">About dir</a></li>'.
                '<li><a id="l7" href="#aaa">Anchor</a></li>'.
                '<li><a id="18" href="javascript:void(0)">Do nothing</a></li>'.
                '<li><a id="19" href="tel:12345678">Call</a></li>'.
                '</ul>'.
                '<a id="l4" href="contact.html"></a>'.
                '<div class="sc-content-cnt1"><a id="l5" href="home.html">home</a></div>'.
                '<div class="sc-content-cnt2"><a id="20" href="/files/somefile-sc56d847895fa33-960.pdf">File</a></div>'.
                '</body></html>';

        $page = new Draft($html);

        phpQuery::newDocument($page->render('admin.php'));

        $this->assertEquals('admin.php?page=about.html', phpQuery::pq('#l1')->elements[0]->getAttribute('href'));
        $this->assertEquals('admin.php?page=doc.html', phpQuery::pq('#l2')->elements[0]->getAttribute('href'));
        $this->assertEquals('http://google.com', phpQuery::pq('#l3')->elements[0]->getAttribute('href'));
        $this->assertEquals('admin.php?page=contact.html', phpQuery::pq('#l4')->elements[0]->getAttribute('href'));
        $this->assertEquals('home.html', phpQuery::pq('#l5')->elements[0]->getAttribute('href'));
        $this->assertEquals('admin.php?page=about', phpQuery::pq('#l6')->elements[0]->getAttribute('href'));
        $this->assertEquals('#aaa', phpQuery::pq('#l7')->elements[0]->getAttribute('href'));
        $this->assertEquals('javascript:void(0)', phpQuery::pq('#18')->elements[0]->getAttribute('href'));
        $this->assertEquals('tel:12345678', phpQuery::pq('#19')->elements[0]->getAttribute('href'));
        $this->assertEquals('/files/somefile-sc56d847895fa33-960.pdf', phpQuery::pq('#20')->elements[0]->getAttribute('href'));
    }
}