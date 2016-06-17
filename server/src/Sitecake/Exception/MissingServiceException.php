<?php

namespace Sitecake\Exception;

class MissingServiceException extends Exception
{
    protected $_messageTemplate = 'Service class %s could not be found';
}