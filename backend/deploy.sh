#!/bin/bash
# deploy.sh — Script de deploy para Render

set -e

echo "🚀 Iniciando deploy do QR Pro API..."

if ! java -version 2>&1 | grep -q "21"; then
    echo "❌ Java 21 não encontrado."
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado."
    exit 1
fi

echo "📦 Compilando projeto..."
mvn clean package -DskipTests

if [ ! -f "target/qrpro-api-1.0.0.jar" ]; then
    echo "❌ JAR não encontrado."
    exit 1
fi

echo "✅ Build concluído!"
echo "📦 JAR: target/qrpro-api-1.0.0.jar"
echo ""
echo "📝 Comando de start no Render:"
echo "   java -Dspring.profiles.active=prod -jar target/qrpro-api-1.0.0.jar"
