<?php

namespace Sitecake\Exception;

class MissingActionException extends Exception
{
    protected $_messageTemplate = 'Action %s not implemented for service %s';
}