<?php

namespace Sitecake\Exception;

class BadArgumentException extends Exception
{
    protected $_messageTemplate = 'Argument \'%s\' is not formatted right.';
}