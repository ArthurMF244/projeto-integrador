<?php

declare(strict_types=1);

require __DIR__ . '/_helpers.php';

try {
    $pdo = db();

    if (method() === 'GET') {
        $config = $pdo->query(
            'SELECT id, nome_sistema, tema, email_suporte FROM configuracoes WHERE id = 1'
        )->fetch();
        jsonResponse(['data' => $config ?: null]);
    }

    if (in_array(method(), ['POST', 'PUT'], true)) {
        $data = requestData();
        requireFields($data, ['nome_sistema', 'email_suporte']);

        $email = filter_var(trim((string) $data['email_suporte']), FILTER_VALIDATE_EMAIL);
        if ($email === false) {
            jsonResponse(['erro' => 'E-mail de suporte inválido.'], 422);
        }

        $stmt = $pdo->prepare(<<<SQL
            INSERT INTO configuracoes (id, nome_sistema, tema, email_suporte)
            VALUES (1, :nome_sistema, :tema, :email_suporte)
            ON DUPLICATE KEY UPDATE
                nome_sistema = VALUES(nome_sistema),
                tema = VALUES(tema),
                email_suporte = VALUES(email_suporte)
        SQL);
        $stmt->execute([
            ':nome_sistema' => trim((string) $data['nome_sistema']),
            ':tema' => choice('tema', $data['tema'] ?? '', ['', 'dark']),
            ':email_suporte' => $email,
        ]);

        jsonResponse(['mensagem' => 'Configurações salvas com sucesso.']);
    }

    jsonResponse(['erro' => 'Método não permitido.'], 405);
} catch (Throwable $e) {
    internalError($e);
}
