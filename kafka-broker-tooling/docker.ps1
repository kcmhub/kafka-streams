# Kafka Broker Tooling - Docker Compose Management Script
# Usage: .\docker.ps1 <command>

param(
    [Parameter(Position=0)]
    [string]$Command = "help"
)

function Show-Help {
    Write-Host "Kafka Broker Tooling - Docker Compose Commands" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Available commands:" -ForegroundColor Yellow
    Write-Host "  .\docker.ps1 up        - Start all services"
    Write-Host "  .\docker.ps1 down      - Stop and remove containers"
    Write-Host "  .\docker.ps1 restart   - Restart all services"
    Write-Host "  .\docker.ps1 logs      - View logs from all services"
    Write-Host "  .\docker.ps1 ps        - List running containers"
    Write-Host "  .\docker.ps1 status    - Show service status"
    Write-Host "  .\docker.ps1 health    - Check health of services"
    Write-Host "  .\docker.ps1 clean     - Stop and remove containers + volumes (DESTRUCTIVE)"
    Write-Host ""
    Write-Host "Service-specific logs:" -ForegroundColor Yellow
    Write-Host "  .\docker.ps1 logs-kafka    - View Kafka logs"
    Write-Host "  .\docker.ps1 logs-kcm-api  - View KCM API logs"
    Write-Host "  .\docker.ps1 logs-kcm-ui   - View KCM UI logs"
    Write-Host ""
    Write-Host "Quick access:" -ForegroundColor Green
    Write-Host "  KCM UI:  http://localhost:3000"
    Write-Host "  KCM API: http://localhost:8080"
    Write-Host "  Kafka:   localhost:9092"
}

function Start-Services {
    Write-Host "Starting all services..." -ForegroundColor Cyan
    docker compose up -d
    Write-Host ""
    Write-Host "✅ Services starting..." -ForegroundColor Green
    Write-Host "Wait 30-60 seconds for initialization" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Access services:" -ForegroundColor Green
    Write-Host "  - KCM UI:  http://localhost:3000"
    Write-Host "  - KCM API: http://localhost:8080"
    Write-Host "  - Kafka:   localhost:9092"
}

function Stop-Services {
    Write-Host "Stopping all services..." -ForegroundColor Cyan
    docker compose down
    Write-Host "✅ Services stopped" -ForegroundColor Green
}

function Restart-Services {
    Write-Host "Restarting all services..." -ForegroundColor Cyan
    docker compose restart
    Write-Host "✅ Services restarted" -ForegroundColor Green
}

function Show-Logs {
    Write-Host "Showing logs (Ctrl+C to exit)..." -ForegroundColor Cyan
    docker compose logs -f
}

function Show-Status {
    Write-Host "Service status:" -ForegroundColor Cyan
    docker compose ps
}

function Check-Health {
    Write-Host "Checking service health..." -ForegroundColor Cyan
    Write-Host ""

    # Check Kafka
    Write-Host "Kafka Broker:" -ForegroundColor Yellow
    try {
        docker compose exec -T kafka kafka-broker-api-versions --bootstrap-server localhost:9092 2>$null | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Healthy" -ForegroundColor Green
        } else {
            Write-Host "❌ Not ready" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ Not ready" -ForegroundColor Red
    }
    Write-Host ""

    # Check KCM API
    Write-Host "KCM API:" -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ Healthy" -ForegroundColor Green
        } else {
            Write-Host "❌ Not ready" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ Not ready" -ForegroundColor Red
    }
    Write-Host ""

    # Check PostgreSQL
    Write-Host "PostgreSQL:" -ForegroundColor Yellow
    try {
        docker compose exec -T postgres pg_isready -U kcm_user 2>$null | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Healthy" -ForegroundColor Green
        } else {
            Write-Host "❌ Not ready" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ Not ready" -ForegroundColor Red
    }
    Write-Host ""

    # Check Redis
    Write-Host "Redis:" -ForegroundColor Yellow
    try {
        $result = docker compose exec -T redis redis-cli ping 2>$null
        if ($result -like "*PONG*") {
            Write-Host "✅ Healthy" -ForegroundColor Green
        } else {
            Write-Host "❌ Not ready" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ Not ready" -ForegroundColor Red
    }
}

function Clean-All {
    Write-Host "⚠️  WARNING: This will delete all data (topics, database, cache)" -ForegroundColor Red
    $confirmation = Read-Host "Type 'yes' to confirm"
    if ($confirmation -eq 'yes') {
        Write-Host "Cleaning up..." -ForegroundColor Cyan
        docker compose down -v
        Write-Host "✅ All containers and volumes removed" -ForegroundColor Green
    } else {
        Write-Host "Cancelled" -ForegroundColor Yellow
    }
}

function Show-ServiceLogs {
    param([string]$Service)
    Write-Host "Showing logs for $Service (Ctrl+C to exit)..." -ForegroundColor Cyan
    docker compose logs -f $Service
}

# Main command dispatcher
switch ($Command.ToLower()) {
    "help" { Show-Help }
    "up" { Start-Services }
    "down" { Stop-Services }
    "restart" { Restart-Services }
    "logs" { Show-Logs }
    "ps" { Show-Status }
    "status" { Show-Status }
    "health" { Check-Health }
    "clean" { Clean-All }
    "logs-kafka" { Show-ServiceLogs -Service "kafka" }
    "logs-kcm-api" { Show-ServiceLogs -Service "kcm-api" }
    "logs-kcm-ui" { Show-ServiceLogs -Service "kcm-ui" }
    "logs-postgres" { Show-ServiceLogs -Service "postgres" }
    "logs-redis" { Show-ServiceLogs -Service "redis" }
    default {
        Write-Host "Unknown command: $Command" -ForegroundColor Red
        Write-Host ""
        Show-Help
    }
}

