<?php

declare(strict_types=1);

require __DIR__ . '/_helpers.php';

try {
    $pdo = db();

    if (method() === 'GET') {
        $sql = 'SELECT id, nome, email, setor, perfil, status, created_at FROM usuarios';
        if (boolParam('ativos')) {
            $sql .= " WHERE status = 'Ativo'";
        }
        $sql .= ' ORDER BY nome';

        jsonResponse(['data' => $pdo->query($sql)->fetchAll()]);
    }

    if (method() === 'POST') {
        $data = requestData();
        requireFields($data, ['nome', 'email', 'setor']);

        $email = filter_var(trim((string) $data['email']), FILTER_VALIDATE_EMAIL);
        if ($email === false) {
            jsonResponse(['erro' => 'E-mail inválido.'], 422);
        }

        $stmt = $pdo->prepare(<<<SQL
            INSERT INTO usuarios (nome, email, setor, perfil, status)
            VALUES (:nome, :email, :setor, :perfil, :status)
        SQL);
        $stmt->execute([
            ':nome' => trim((string) $data['nome']),
            ':email' => $email,
            ':setor' => trim((string) $data['setor']),
            ':perfil' => choice('perfil', $data['perfil'] ?? null, ['Administrador', 'Atendente', 'Solicitante'], 'Solicitante'),
            ':status' => choice('status', $data['status'] ?? null, ['Ativo', 'Inativo'], 'Ativo'),
        ]);

        jsonResponse(['mensagem' => 'Usuário criado com sucesso.', 'id' => (int) $pdo->lastInsertId()], 201);
    }

    jsonResponse(['erro' => 'Método não permitido.'], 405);
} catch (PDOException $e) {
    if ((string) $e->getCode() === '23000') {
        jsonResponse(['erro' => 'Já existe um usuário com este e-mail.'], 409);
    }
    internalError($e);
} catch (Throwable $e) {
    internalError($e);
}
