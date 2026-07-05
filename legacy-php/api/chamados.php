<?php

declare(strict_types=1);

require __DIR__ . '/_helpers.php';

const CHAMADO_STATUS = ['Novo', 'Em atendimento', 'Aguardando retorno', 'Finalizado'];
const CHAMADO_PRIORIDADES = ['Baixa', 'Média', 'Alta', 'Crítica'];
const CHAMADO_IMPACTOS = ['Baixo', 'Médio', 'Alto', 'Geral'];

try {
    $pdo = db();
    $id = getId();

    if (method() === 'GET' && $id !== null) {
        $stmt = $pdo->prepare('SELECT * FROM chamados WHERE id = ?');
        $stmt->execute([$id]);
        $chamado = $stmt->fetch();

        if (!$chamado) {
            jsonResponse(['erro' => 'Chamado não encontrado.'], 404);
        }

        $movimentacoes = $pdo->prepare(
            'SELECT * FROM chamado_movimentacoes
             WHERE chamado_id = ?
             ORDER BY criado_em DESC, id DESC'
        );
        $movimentacoes->execute([$id]);

        jsonResponse([
            'data' => $chamado,
            'movimentacoes' => $movimentacoes->fetchAll(),
        ]);
    }

    if (method() === 'GET') {
        $where = [];
        $params = [];

        $filters = [
            'status' => 'c.status',
            'prioridade' => 'c.prioridade',
            'area' => 'c.area_responsavel',
            'responsavel' => 'c.responsavel_nome',
            'solicitante' => 'c.solicitante_nome',
        ];

        foreach ($filters as $queryParam => $column) {
            if (isset($_GET[$queryParam]) && trim((string) $_GET[$queryParam]) !== '') {
                $placeholder = ':' . $queryParam;
                $where[] = "{$column} = {$placeholder}";
                $params[$placeholder] = trim((string) $_GET[$queryParam]);
            }
        }

        if (isset($_GET['q']) && trim((string) $_GET['q']) !== '') {
            $where[] = '(c.titulo LIKE :q OR c.descricao LIKE :q OR c.solicitante_nome LIKE :q OR c.responsavel_nome LIKE :q)';
            $params[':q'] = '%' . trim((string) $_GET['q']) . '%';
        }

        $whereSql = $where ? 'WHERE ' . implode(' AND ', $where) : '';

        $stmt = $pdo->prepare(<<<SQL
            SELECT
                c.id,
                c.titulo,
                c.status,
                c.prioridade,
                c.impacto,
                c.categoria,
                c.area_solicitante,
                c.area_responsavel,
                c.solicitante_id,
                c.solicitante_nome,
                c.responsavel_id,
                c.responsavel_nome,
                c.aberto_em,
                c.finalizado_em
            FROM chamados c
            {$whereSql}
            ORDER BY c.id DESC
        SQL);
        $stmt->execute($params);

        $kpis = $pdo->query(<<<SQL
            SELECT
                SUM(status <> 'Finalizado') AS abertos,
                SUM(status = 'Em atendimento') AS atendimento,
                SUM(status = 'Finalizado') AS finalizados
            FROM chamados
        SQL)->fetch();

        jsonResponse([
            'data' => $stmt->fetchAll(),
            'kpis' => [
                'abertos' => (int) ($kpis['abertos'] ?? 0),
                'atendimento' => (int) ($kpis['atendimento'] ?? 0),
                'finalizados' => (int) ($kpis['finalizados'] ?? 0),
            ],
        ]);
    }

    if (method() === 'POST') {
        $data = requestData();
        requireFields($data, ['titulo', 'area_solicitante', 'area_responsavel', 'categoria', 'prioridade', 'descricao']);

        $solicitanteId = normalizeNullableInt($data['solicitante_id'] ?? null);
        $responsavelId = normalizeNullableInt($data['responsavel_id'] ?? null);
        $solicitanteNome = findUserName($solicitanteId)
            ?? trim((string) ($data['solicitante_nome'] ?? ''))
            ?: 'Não informado';
        $responsavelNome = findUserName($responsavelId)
            ?? (trim((string) ($data['responsavel_nome'] ?? '')) ?: null);
        $prioridade = choice('prioridade', $data['prioridade'] ?? null, CHAMADO_PRIORIDADES, 'Média');
        $impacto = choice('impacto', $data['impacto'] ?? null, CHAMADO_IMPACTOS, 'Baixo');
        $status = choice('status', $data['status'] ?? null, CHAMADO_STATUS, 'Novo');

        $pdo->beginTransaction();

        $stmt = $pdo->prepare(<<<SQL
            INSERT INTO chamados (
                titulo, solicitante_id, solicitante_nome, area_solicitante,
                area_responsavel, responsavel_id, responsavel_nome, categoria,
                prioridade, impacto, status, descricao
            ) VALUES (
                :titulo, :solicitante_id, :solicitante_nome, :area_solicitante,
                :area_responsavel, :responsavel_id, :responsavel_nome, :categoria,
                :prioridade, :impacto, :status, :descricao
            )
        SQL);
        $stmt->execute([
            ':titulo' => trim((string) $data['titulo']),
            ':solicitante_id' => $solicitanteId,
            ':solicitante_nome' => $solicitanteNome,
            ':area_solicitante' => trim((string) $data['area_solicitante']),
            ':area_responsavel' => trim((string) $data['area_responsavel']),
            ':responsavel_id' => $responsavelId,
            ':responsavel_nome' => $responsavelNome,
            ':categoria' => trim((string) $data['categoria']),
            ':prioridade' => $prioridade,
            ':impacto' => $impacto,
            ':status' => $status,
            ':descricao' => trim((string) $data['descricao']),
        ]);

        $chamadoId = (int) $pdo->lastInsertId();
        $movimentacao = $pdo->prepare(<<<SQL
            INSERT INTO chamado_movimentacoes (
                chamado_id, status, prioridade, area_responsavel,
                responsavel_id, responsavel_nome, descricao
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        SQL);
        $movimentacao->execute([
            $chamadoId,
            $status,
            $prioridade,
            trim((string) $data['area_responsavel']),
            $responsavelId,
            $responsavelNome,
            'Chamado criado.',
        ]);

        $pdo->commit();
        jsonResponse(['mensagem' => 'Chamado criado com sucesso.', 'id' => $chamadoId], 201);
    }

    if (in_array(method(), ['PUT', 'PATCH'], true) && $id !== null) {
        $data = requestData();

        $stmt = $pdo->prepare('SELECT * FROM chamados WHERE id = ?');
        $stmt->execute([$id]);
        $atual = $stmt->fetch();

        if (!$atual) {
            jsonResponse(['erro' => 'Chamado não encontrado.'], 404);
        }

        $responsavelIdValue = array_key_exists('responsavel_id', $data)
            ? $data['responsavel_id']
            : $atual['responsavel_id'];
        $responsavelId = normalizeNullableInt($responsavelIdValue);
        $responsavelNome = $responsavelId === null
            ? null
            : findUserName($responsavelId)
                ?? (trim((string) ($data['responsavel_nome'] ?? '')) ?: $atual['responsavel_nome']);
        $status = choice('status', $data['status'] ?? $atual['status'], CHAMADO_STATUS);
        $prioridade = choice('prioridade', $data['prioridade'] ?? $atual['prioridade'], CHAMADO_PRIORIDADES);
        $areaResponsavel = trim((string) ($data['area_responsavel'] ?? $atual['area_responsavel']));

        if ($areaResponsavel === '') {
            jsonResponse(['erro' => 'Campo obrigatório: area_responsavel'], 422);
        }

        $finalizadoEm = $status === 'Finalizado'
            ? ($atual['finalizado_em'] ?: date('Y-m-d H:i:s'))
            : null;

        $pdo->beginTransaction();

        $update = $pdo->prepare(<<<SQL
            UPDATE chamados
            SET status = :status,
                prioridade = :prioridade,
                area_responsavel = :area_responsavel,
                responsavel_id = :responsavel_id,
                responsavel_nome = :responsavel_nome,
                finalizado_em = :finalizado_em
            WHERE id = :id
        SQL);
        $update->execute([
            ':status' => $status,
            ':prioridade' => $prioridade,
            ':area_responsavel' => $areaResponsavel,
            ':responsavel_id' => $responsavelId,
            ':responsavel_nome' => $responsavelNome,
            ':finalizado_em' => $finalizadoEm,
            ':id' => $id,
        ]);

        $movimentacao = $pdo->prepare(<<<SQL
            INSERT INTO chamado_movimentacoes (
                chamado_id, status, prioridade, area_responsavel,
                responsavel_id, responsavel_nome, descricao
            ) VALUES (
                :chamado_id, :status, :prioridade, :area_responsavel,
                :responsavel_id, :responsavel_nome, :descricao
            )
        SQL);
        $movimentacao->execute([
            ':chamado_id' => $id,
            ':status' => $status,
            ':prioridade' => $prioridade,
            ':area_responsavel' => $areaResponsavel,
            ':responsavel_id' => $responsavelId,
            ':responsavel_nome' => $responsavelNome,
            ':descricao' => trim((string) ($data['descricao_movimentacao'] ?? '')) ?: 'Movimentação registrada.',
        ]);

        $pdo->commit();
        jsonResponse(['mensagem' => 'Chamado atualizado com sucesso.']);
    }

    jsonResponse(['erro' => 'Método não permitido.'], 405);
} catch (Throwable $e) {
    if (isset($pdo) && $pdo instanceof PDO && $pdo->inTransaction()) {
        $pdo->rollBack();
    }
    internalError($e);
}
