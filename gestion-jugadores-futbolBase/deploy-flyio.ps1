# Script de inicio rápido para Fly.io
# Ejecutar línea por línea en PowerShell

# 1. Instalar Fly CLI (si no lo tienes)
Write-Host "=== INSTALANDO FLY CLI ===" -ForegroundColor Green
iwr https://fly.io/install.ps1 -useb | iex

# 2. Login en Fly.io
Write-Host "`n=== LOGIN EN FLY.IO ===" -ForegroundColor Green
Write-Host "Te abrirá el navegador para autenticarte" -ForegroundColor Yellow
fly auth login

# 3. Crear la aplicación (cambia el nombre si está tomado)
Write-Host "`n=== CREANDO APLICACIÓN ===" -ForegroundColor Green
$appName = "gestion-jugadores-api"
Write-Host "Nombre de la app: $appName" -ForegroundColor Cyan
fly apps create $appName --region mad

# 4. Crear base de datos PostgreSQL
Write-Host "`n=== CREANDO BASE DE DATOS ===" -ForegroundColor Green
$dbName = "gestion-jugadores-db"
Write-Host "Nombre de la DB: $dbName" -ForegroundColor Cyan
fly postgres create --name $dbName --region mad

# 5. Adjuntar base de datos a la aplicación
Write-Host "`n=== ADJUNTANDO BASE DE DATOS ===" -ForegroundColor Green
fly postgres attach $dbName --app $appName

# 6. Configurar secrets
Write-Host "`n=== CONFIGURANDO SECRETS ===" -ForegroundColor Green

# JWT Secret
Write-Host "`nConfigura tu JWT Secret (mínimo 64 caracteres):" -ForegroundColor Yellow
$jwtSecret = Read-Host "JWT_SECRET"
if ($jwtSecret) {
    fly secrets set JWT_SECRET="$jwtSecret" --app $appName
}

# OpenAI API Key
Write-Host "`nConfigura tu OpenAI API Key (obtén una en https://platform.openai.com/api-keys):" -ForegroundColor Yellow
$openaiKey = Read-Host "OPENAI_API_KEY"
if ($openaiKey) {
    fly secrets set OPENAI_API_KEY="$openaiKey" --app $appName
}

# CORS Origins
Write-Host "`nConfigura los orígenes CORS permitidos (separados por coma):" -ForegroundColor Yellow
Write-Host "Ejemplo: https://mi-frontend.com,https://mi-app.com" -ForegroundColor Gray
$corsOrigins = Read-Host "CORS_ALLOWED_ORIGINS"
if ($corsOrigins) {
    fly secrets set CORS_ALLOWED_ORIGINS="$corsOrigins" --app $appName
}

# 7. Deploy
Write-Host "`n=== HACIENDO DEPLOY ===" -ForegroundColor Green
Write-Host "Esto puede tardar varios minutos..." -ForegroundColor Yellow
fly deploy

# 8. Verificar
Write-Host "`n=== VERIFICANDO DEPLOY ===" -ForegroundColor Green
fly status --app $appName

Write-Host "`n=== ¡DEPLOY COMPLETADO! ===" -ForegroundColor Green
Write-Host "Tu API está disponible en: https://$appName.fly.dev" -ForegroundColor Cyan
Write-Host "`nComandos útiles:" -ForegroundColor Yellow
Write-Host "  fly logs               - Ver logs en tiempo real" -ForegroundColor Gray
Write-Host "  fly status             - Ver estado de la app" -ForegroundColor Gray
Write-Host "  fly open               - Abrir en el navegador" -ForegroundColor Gray
Write-Host "  fly ssh console        - Conectar por SSH" -ForegroundColor Gray
