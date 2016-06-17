<?php

namespace Sitecake\Exception;

class InternalException extends Exception
{
    protected $_messageTemplate = 'SiteCake Internal Server Error : %s';
}