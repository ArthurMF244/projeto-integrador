<?php

declare(strict_types=1);

require __DIR__ . '/_helpers.php';

try {
    db()->query('SELECT 1');
    jsonResponse(['status' => 'ok']);
} catch (Throwable $e) {
    error_log($e->__toString());
    jsonResponse(['status' => 'error'], 503);
}
