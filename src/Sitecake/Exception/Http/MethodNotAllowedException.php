<?php

namespace Sitecake\Exception\Http;

use Sitecake\Exception\Exception;

class MethodNotAllowedException extends Exception
{
    protected $_messageTemplate = '405 Method Not Allowed : %s';

    public function __construct($message, $code = 405, $previous = null)
    {
        parent::__construct($message, 405, $previous);
    }
}