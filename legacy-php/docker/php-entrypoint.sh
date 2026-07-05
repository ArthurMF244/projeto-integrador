#!/bin/sh
set -e

echo "Iniciando container PHP..."

DB_HOST="${DB_HOST:-mysql}"
DB_PORT="${DB_PORT:-3306}"
DB_DATABASE="${DB_DATABASE:-si_chamados}"
DB_USERNAME="${DB_USERNAME:-si_chamados}"
DB_PASSWORD="${DB_PASSWORD:-si_chamados}"

echo "Aguardando MySQL em ${DB_HOST}:${DB_PORT}..."

until php -r "
\$host = getenv('DB_HOST') ?: 'mysql';
\$port = (int) (getenv('DB_PORT') ?: 3306);
\$conn = @fsockopen(\$host, \$port, \$errno, \$errstr, 2);
if (!\$conn) {
    exit(1);
}
fclose(\$conn);
exit(0);
"; do
    echo "MySQL ainda não está pronto. Tentando novamente..."
    sleep 2
done

echo "MySQL disponível."

if [ "$RUN_MIGRATIONS" = "true" ]; then
    echo "RUN_MIGRATIONS=true"

    if [ -f "/var/www/html/migrations/001_create_tables.sql" ]; then
        echo "Executando migrations..."

        MYSQL_PWD="$DB_PASSWORD" mysql \
            -h "$DB_HOST" \
            -P "$DB_PORT" \
            -u "$DB_USERNAME" \
            "$DB_DATABASE" < /var/www/html/migrations/001_create_tables.sql

        echo "Migrations executadas com sucesso."
    else
        echo "Nenhum arquivo de migration encontrado em migrations/001_create_tables.sql"
    fi
else
    echo "RUN_MIGRATIONS não está ativo. Pulando migrations."
fi

echo "Subindo Apache..."

exec "$@"