<?php

namespace Sitecake\Exception;

class FileNotFoundException extends Exception
{
	protected $_messageTemplate = '%s file "%s" not found';
}