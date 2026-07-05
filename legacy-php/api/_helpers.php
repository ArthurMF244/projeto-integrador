<?php

declare(strict_types=1);

require_once dirname(__DIR__) . '/database/connection.php';

function jsonResponse(array $payload, int $status = 200): void
{
    http_response_code($status);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

function requestData(): array
{
    $contentType = $_SERVER['CONTENT_TYPE'] ?? '';

    if (str_contains($contentType, 'application/json')) {
        $raw = file_get_contents('php://input');
        try {
            $data = json_decode($raw ?: '{}', true, 512, JSON_THROW_ON_ERROR);
        } catch (JsonException) {
            jsonResponse(['erro' => 'JSON inválido.'], 400);
        }

        return is_array($data) ? $data : [];
    }

    return $_POST ?: [];
}

function only(array $data, array $keys): array
{
    $result = [];
    foreach ($keys as $key) {
        $result[$key] = $data[$key] ?? null;
    }
    return $result;
}

function method(): string
{
    return strtoupper($_SERVER['REQUEST_METHOD'] ?? 'GET');
}

function getId(): ?int
{
    $id = $_GET['id'] ?? null;
    if ($id === null || $id === '') {
        return null;
    }
    $validated = filter_var($id, FILTER_VALIDATE_INT, ['options' => ['min_range' => 1]]);
    return $validated === false ? null : (int) $validated;
}

function boolParam(string $name): bool
{
    return isset($_GET[$name]) && in_array(strtolower((string) $_GET[$name]), ['1', 'true', 'sim', 'yes'], true);
}

function normalizeNullableInt(mixed $value): ?int
{
    if ($value === null || $value === '') {
        return null;
    }
    $validated = filter_var($value, FILTER_VALIDATE_INT, ['options' => ['min_range' => 1]]);
    return $validated === false ? null : (int) $validated;
}

function choice(string $field, mixed $value, array $allowed, ?string $default = null): string
{
    $normalized = trim((string) ($value ?? ''));
    if ($normalized === '' && $default !== null) {
        return $default;
    }

    if (!in_array($normalized, $allowed, true)) {
        jsonResponse(['erro' => "Valor inválido para {$field}."], 422);
    }

    return $normalized;
}

function requireFields(array $data, array $fields): void
{
    foreach ($fields as $field) {
        if (!isset($data[$field]) || trim((string) $data[$field]) === '') {
            jsonResponse(['erro' => "Campo obrigatório: {$field}"], 422);
        }
    }
}

function findUserName(?int $id): ?string
{
    if (!$id) {
        return null;
    }

    $stmt = db()->prepare('SELECT nome FROM usuarios WHERE id = ?');
    $stmt->execute([$id]);
    $nome = $stmt->fetchColumn();

    return $nome !== false ? (string) $nome : null;
}

function internalError(Throwable $e): never
{
    error_log($e->__toString());
    jsonResponse(['erro' => 'Erro interno do servidor.'], 500);
}
