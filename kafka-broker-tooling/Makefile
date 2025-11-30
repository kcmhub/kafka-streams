.PHONY: help up down restart logs status clean ps health

# Default target
help:
	@echo "Kafka Broker Tooling - Docker Compose Commands"
	@echo ""
	@echo "Available commands:"
	@echo "  make up        - Start all services"
	@echo "  make down      - Stop and remove containers"
	@echo "  make restart   - Restart all services"
	@echo "  make logs      - View logs from all services"
	@echo "  make ps        - List running containers"
	@echo "  make status    - Show service status"
	@echo "  make health    - Check health of services"
	@echo "  make clean     - Stop and remove containers + volumes (DESTRUCTIVE)"
	@echo ""
	@echo "Service-specific logs:"
	@echo "  make logs-kafka    - View Kafka logs"
	@echo "  make logs-kcm-api  - View KCM API logs"
	@echo "  make logs-kcm-ui   - View KCM UI logs"
	@echo ""
	@echo "Quick access:"
	@echo "  KCM UI:  http://localhost:4000"
	@echo "  KCM API: http://localhost:8080"
	@echo "  Kafka:   localhost:9092"

# Start all services
up:
	docker compose up -d
	@echo ""
	@echo "✅ Services starting..."
	@echo "Wait 30-60 seconds for initialization"
	@echo ""
	@echo "Access services:"
	@echo "  - KCM UI:  http://localhost:4000"
	@echo "  - KCM API: http://localhost:8080"
	@echo "  - Kafka:   localhost:9092"

# Stop and remove containers
down:
	docker compose down

# Restart all services
restart:
	docker compose restart

# View logs from all services
logs:
	docker compose logs -f

# List running containers
ps:
	docker compose ps

# Show service status
status:
	docker compose ps

# Check health of services
health:
	@echo "Checking service health..."
	@echo ""
	@echo "Kafka Broker:"
	@docker compose exec -T kafka kafka-broker-api-versions --bootstrap-server localhost:9092 2>/dev/null && echo "✅ Healthy" || echo "❌ Not ready"
	@echo ""
	@echo "KCM API:"
	@curl -s http://localhost:8080/actuator/health | grep -q "UP" && echo "✅ Healthy" || echo "❌ Not ready"
	@echo ""
	@echo "PostgreSQL:"
	@docker compose exec -T postgres pg_isready -U kcm_user 2>/dev/null && echo "✅ Healthy" || echo "❌ Not ready"
	@echo ""
	@echo "Redis:"
	@docker compose exec -T redis redis-cli ping 2>/dev/null | grep -q "PONG" && echo "✅ Healthy" || echo "❌ Not ready"

# Stop and remove containers + volumes (DESTRUCTIVE)
clean:
	@echo "⚠️  WARNING: This will delete all data (topics, database, cache)"
	@echo "Press Ctrl+C to cancel, or Enter to continue..."
	@read dummy
	docker compose down -v
	@echo "✅ All containers and volumes removed"

# Service-specific logs
logs-kafka:
	docker compose logs -f kafka

logs-kcm-api:
	docker compose logs -f kcm-api

logs-kcm-ui:
	docker compose logs -f kcm-ui

logs-postgres:
	docker compose logs -f postgres

logs-redis:
	docker compose logs -f redis

# Restart specific services
restart-kafka:
	docker compose restart kafka

restart-kcm-api:
	docker compose restart kcm-api

restart-kcm-ui:
	docker compose restart kcm-ui

