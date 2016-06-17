<?php

$app['debug'] = true;

$app['BASE_DIR'] = realpath(__DIR__ . '/test-site/');

$app['CREDENTIALS_PATH'] = 'credentials.php';

$app['log'] = false;

$app['error.level'] = E_ALL;

$app['site.default_pages'] = ['index.php', 'index.html'];

// Use false to avoid url prefixing with phpunit path
$app['pages.use_document_relative_paths'] = false;