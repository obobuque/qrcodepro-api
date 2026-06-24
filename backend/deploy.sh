#!/bin/bash
set -e
echo "🚀 Deploy QR Pro API..."
java -version 2>&1 | grep -q "21" || { echo "❌ Java 21 não encontrado"; exit 1; }
command -v mvn &> /dev/null || { echo "❌ Maven não encontrado"; exit 1; }
mvn clean package -DskipTests
[ -f "target/qrpro-api-1.0.0.jar" ] || { echo "❌ JAR não encontrado"; exit 1; }
echo "✅ Build OK: target/qrpro-api-1.0.0.jar"
echo "📝 Start: java -Dspring.profiles.active=prod -jar target/qrpro-api-1.0.0.jar"
