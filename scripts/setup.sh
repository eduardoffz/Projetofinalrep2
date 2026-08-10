#!/bin/bash
echo "=== Setup Frota Agricola ==="

echo "Criando banco de dados..."
mysql -u root -p < init-database.sql

echo "Build do projeto (raiz)..."
cd ..
mvn clean install -DskipTests

echo "Executando aplicacao..."
cd frota-web
mvn spring-boot:run
