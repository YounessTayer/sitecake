<?php

namespace Sitecake\Auth;

use League\Flysystem\Filesystem;

class Auth implements AuthInterface
{

    /**
     * @var Filesystem
     */
    protected $_fs;

    /**
     * @var string Path to file with credentials
     */
    protected $_credentialsFile;

    /**
     * @var string Credential string
     */
    protected $_credentials;

    public function __construct(Filesystem $fs, $credentialsFile)
    {
        $this->_fs = $fs;
        $this->_credentialsFile = $credentialsFile;
        $this->_readCredentials();
    }

    public function authenticate($credentials)
    {
        return ($credentials === $this->_credentials);
    }

    public function setCredentials($credentials)
    {
        $this->_credentials = $credentials;
        $this->_fs->put($this->_credentialsFile, '<?php $credentials = "'.$credentials.'";');
    }

    protected function _readCredentials()
    {
        $txt = $this->_fs->read($this->_credentialsFile);
        preg_match_all('/\$credentials\s*=\s*"([^"]+)"/', $txt, $matches);
        $this->_credentials = $matches[1][0];
    }
}