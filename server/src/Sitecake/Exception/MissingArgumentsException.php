<?php

namespace Sitecake\Exception;

class MissingArgumentsException extends Exception
{
    protected $_messageTemplate = 'Argument \'%s\' not passed in request.';
}