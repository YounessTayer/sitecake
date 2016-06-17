<?php
namespace Sitecake;

use Sitecake\Util\HtmlUtils;

class Renderer
{
    /**
     * @var array Options with paths
     */
    protected $_options;

    /**
     * @var Site Reference to Site object
     */
    protected $_site;

    public function __construct($_site, $options)
    {
        $this->_site = $_site;
        $this->_options = $options;
    }

    public function loginResponse()
    {
        return $this->injectLoginDialog($this->_site->getDefaultPublicPage());
    }

    public function editResponse($page)
    {
        $this->_site->startEdit();

        return $this->injectEditorCode(
            $this->_site->getDraft($page),
            $this->_site->isDraftClean()
        );
    }

    /**
     * @param Draft $draft
     *
     * @return mixed
     */
    protected function injectLoginDialog($draft)
    {
        $draft->appendCodeToHead($this->clientCodeLogin());

        return $draft->render($this->_options['entry_point_file_name']);
    }

    /**
     * @param Draft $draft
     * @param bool  $published
     *
     * @return mixed
     */
    protected function injectEditorCode($draft, $published)
    {
        $draft->appendCodeToHead($this->clientCodeEditor($published));

        return $draft->render($this->_options['entry_point_file_name']);
    }

    protected function clientCodeLogin()
    {
        $globals = 'var sitecakeGlobals = {' .
                   "editMode: false, " .
                   'serverVersionId: "${version}", ' .
                   'phpVersion: "' . phpversion() . '@' . PHP_OS . '", ' .
                   'serviceUrl:"' . $this->_options['SERVICE_URL'] . '", ' .
                   'configUrl:"' . $this->_options['EDITOR_CONFIG_URL'] . '", ' .
                   'forceLoginDialog: true' .
                   '};';

        return HtmlUtils::wrapToScriptTag($globals) .
               HtmlUtils::scriptTag($this->_options['EDITOR_LOGIN_URL']);
    }

    protected function clientCodeEditor($published)
    {
        $globals = 'var sitecakeGlobals = {' .
                   'editMode: true, ' .
                   'serverVersionId: "${version}", ' .
                   'phpVersion: "' . phpversion() . '@' . PHP_OS . '", ' .
                   'serviceUrl: "' . $this->_options['SERVICE_URL'] . '", ' .
                   'configUrl: "' . $this->_options['EDITOR_CONFIG_URL'] . '", ' .
                   'draftPublished: ' . ($published ? 'true' : 'false') .
                   '};';

        return HtmlUtils::wrapToScriptTag($globals) .
               HtmlUtils::scriptTag($this->_options['EDITOR_EDIT_URL']);
    }
}