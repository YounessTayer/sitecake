<?php
// Check for php version
$phpVersion = preg_split("/[:.]/", phpversion());
if (($phpVersion[0] * 10 + $phpVersion[1]) < 54)
{
    trigger_error("PHP version $phpVersion[0].$phpVersion[1] is found on your web hosting.
		PHP version 5.4 (or greater) is required.", E_USER_ERROR);
}
// Check if GD is present
if (!extension_loaded('gd'))
{
    trigger_error("GD lib (PHP extension) is required for Sitecake to run.", E_USER_ERROR);
}
// Check if mbstring is present
if (!extension_loaded('mbstring'))
{
    trigger_error("mbstring lib (PHP extension) is required for Sitecake to run.", E_USER_ERROR);
}
// Check if document directory is writable and readable
if(!is_writable('../') || !is_readable('../'))
{
    trigger_error("Root site directory have to be readable and writable.", E_USER_ERROR);
}

// Echo OK message if check.php file is accessed directly
if($_SERVER['PHP_SELF'] == '/sitecake/${version}/config/check.php')
{
    echo "Basic server configuration needed for Sitecake to run is OK";
}