<?php

namespace Sitecake\Exception;

class BadFormatException extends Exception
{
    protected $_messageTemplate = 'SiteCake container (.sc-content-%s) is not contained inside one file.';
}