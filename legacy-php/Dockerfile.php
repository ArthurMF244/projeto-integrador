FROM php:8.2-apache

WORKDIR /var/www/html

# Instala extensões e cliente MySQL
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        default-mysql-client \
        unzip \
        git \
    && docker-php-ext-install pdo pdo_mysql mysqli \
    && a2enmod rewrite \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Configuração do Apache
COPY docker/000-default.conf /etc/apache2/sites-available/000-default.conf

# Copia o entrypoint
COPY docker/php-entrypoint.sh /usr/local/bin/app-entrypoint

# Corrige permissão e possível problema de CRLF do Windows
RUN sed -i 's/\r$//' /usr/local/bin/app-entrypoint \
    && chmod +x /usr/local/bin/app-entrypoint

# Copia os arquivos do projeto
COPY . /var/www/html

# Permissões básicas
RUN chown -R www-data:www-data /var/www/html

ENTRYPOINT ["/usr/local/bin/app-entrypoint"]
CMD ["apache2-foreground"]