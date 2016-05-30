<?php

namespace Sitecake\Exception;

use RuntimeException;

class Exception extends RuntimeException
{
    /**
     * @var array Message attributes
     */
    protected $_attributes;

    /**
     * @var string Message template used if extra parameters needs to be mentioned in exception message
     */
    protected $_messageTemplate;

    public function __construct($message, $code = 500, $previous = null)
    {
        if (is_array($message))
        {
            $this->_attributes = $message;
            $message = vsprintf($this->_messageTemplate, $message);
        }
        else if(is_string($message) && $this->_messageTemplate)
        {
            $message = sprintf($this->_messageTemplate, $message);
        }

        parent::__construct($message, $code, $previous);
    }
}