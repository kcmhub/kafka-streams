# Docker Compose Setup Guide

This guide explains how to use the Docker Compose setup for local Kafka development with KCM (Kafka Cluster Manager).

## 📦 What's Included

The `docker-compose.yml` file provides a complete Kafka development stack:

| Service | Port | Description |
|---------|------|-------------|
| **Kafka Broker** | 9092 | Single Kafka broker (KRaft mode - no Zookeeper) |
| **PostgreSQL** | 5432 | Database for KCM metadata |
| **Redis** | 6379 | Cache for KCM |
| **KCM API** | 8080 | Backend REST API for Kafka management |
| **KCM UI** | 3000 | Web-based UI for Kafka management |

## 🚀 Quick Start

### Option A: Using Helper Scripts (Recommended)

We provide helper scripts to simplify Docker Compose operations:

##### Windows (PowerShell)
```powershell
# See all available commands
.\docker.ps1 help

# Start all services
.\docker.ps1 up

# Check health
.\docker.ps1 health

# View logs
.\docker.ps1 logs

# Stop services
.\docker.ps1 down
```

##### Linux/macOS (Make)
```bash
# See all available commands
make help

# Start all services
make up

# Check health
make health

# View logs
make logs

# Stop services
make down
```

### Option B: Using Docker Compose Directly

### 1. Start all services

```bash
docker compose up -d
```

This will:
- Pull all required Docker images (first time only)
- Start all services in the background
- Create necessary networks and volumes

### 2. Verify services are running

```bash
docker compose ps
```

Expected output:
```
NAME                        STATUS              PORTS
kafka-tooling-broker        Up (healthy)        0.0.0.0:9092->9092/tcp
kafka-tooling-kcm-api       Up (healthy)        0.0.0.0:8080->8080/tcp
kafka-tooling-kcm-ui        Up                  0.0.0.0:3000->80/tcp
kafka-tooling-postgres      Up (healthy)        0.0.0.0:5432->5432/tcp
kafka-tooling-redis         Up (healthy)        0.0.0.0:6379->6379/tcp
```

### 3. Wait for services to be healthy

Services have health checks. Wait 30-60 seconds for everything to initialize.

Check KCM API health:
```bash
curl http://localhost:8080/actuator/health
```

## 🌐 Access the Services

### KCM Web UI
Open your browser and navigate to:
```
http://localhost:3000
```

The UI provides:
- Topic management (create, delete, configure)
- Message producer and consumer
- Consumer group monitoring
- Cluster overview

### KCM API
REST API available at:
```
http://localhost:8080
```

API documentation (if available):
```
http://localhost:8080/swagger-ui.html
```

### Direct Kafka Access
Connect to Kafka from your applications:
```
bootstrap.servers=localhost:9092
```

## 🔍 Monitoring and Logs

### View logs for all services
```bash
docker compose logs -f
```

### View logs for specific service
```bash
docker compose logs -f kafka
docker compose logs -f kcm-api
docker compose logs -f kcm-ui
docker compose logs -f postgres
```

### Follow logs in real-time
```bash
docker compose logs -f --tail=100
```

## 🛠️ Common Operations

### Restart a specific service
```bash
docker compose restart kafka
docker compose restart kcm-api
```

### Stop all services
```bash
docker compose stop
```

### Start stopped services
```bash
docker compose start
```

### Remove containers (keeps volumes)
```bash
docker compose down
```

### Remove containers AND data (clean slate)
```bash
docker compose down -v
```

⚠️ **Warning**: Using `-v` will delete all Kafka topics, PostgreSQL data, and Redis cache!

## 🔧 Configuration

### Environment Variables

Copy `.env.example` to `.env` and customize:
```bash
cp .env.example .env
```

Edit `.env` to change ports or credentials:
```env
KAFKA_BROKER_PORT=9092
POSTGRES_PORT=5432
POSTGRES_PASSWORD=your_secure_password
KCM_UI_PORT=3000
```

Then restart:
```bash
docker compose down
docker compose up -d
```

### Kafka Configuration

The Kafka broker is configured for development with:
- Auto topic creation enabled
- Single broker (replication factor 1)
- Low latency settings
- JMX metrics on port 9101

To modify Kafka settings, edit the `kafka` service environment variables in `docker-compose.yml`.

## 📊 Using with kafka-broker-tooling Module

The Java application in this module can connect to the Docker Kafka:

### Configure application

##### Windows (PowerShell)
```powershell
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
mvn spring-boot:run
```

##### Linux/macOS (Bash)
```bash
export SPRING_KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
mvn spring-boot:run
```

### Use the KafkaBrokerManager

```java
@Autowired
private KafkaBrokerManager brokerManager;

// Create a topic
brokerManager.createTopic("my-topic", 3, (short) 1);

// List topics
brokerManager.listTopics();

// Delete a topic
brokerManager.deleteTopic("my-topic");
```

## 🧪 Testing with KCM

### Create a test topic via UI
1. Open http://localhost:3000
2. Navigate to "Topics"
3. Click "Create Topic"
4. Enter name: `test-topic`
5. Set partitions: `3`
6. Click "Create"

### Produce messages via UI
1. Select your topic
2. Click "Produce"
3. Enter key and value
4. Click "Send"

### Consume messages via UI
1. Select your topic
2. Click "Consume"
3. View messages in real-time

## 🐛 Troubleshooting

### Services won't start
```bash
# Check for port conflicts
docker compose ps
netstat -an | grep "9092\|3000\|8080\|5432\|6379"

# View detailed logs
docker compose logs
```

### KCM API can't connect to Kafka
```bash
# Check Kafka is running
docker compose logs kafka

# Check network connectivity
docker compose exec kcm-api ping kafka
```

### Reset everything
```bash
# Nuclear option: remove everything and start fresh
docker compose down -v
docker system prune -f
docker compose up -d
```

### PostgreSQL connection issues
```bash
# Check PostgreSQL logs
docker compose logs postgres

# Connect to PostgreSQL directly
docker compose exec postgres psql -U kcm_user -d kcm
```

## 📚 Additional Resources

- **KCM GitHub**: https://github.com/kcmhub/KCM
- **Confluent Kafka Docker**: https://docs.confluent.io/platform/current/installation/docker/
- **Docker Compose Docs**: https://docs.docker.com/compose/

## 🔐 Security Notes

⚠️ **This setup is for DEVELOPMENT ONLY**

- Default passwords are used (change in production!)
- No authentication on Kafka (enable SASL/SSL for production)
- Services are exposed on localhost (don't expose to internet)
- No data encryption at rest

For production deployments, consider:
- Using Kafka with SASL/SSL authentication
- Setting strong passwords for PostgreSQL
- Using Docker secrets for credentials
- Enabling Redis authentication
- Running behind a reverse proxy with HTTPS

## 📝 Notes

- **Data Persistence**: PostgreSQL and Redis data are stored in Docker volumes and persist across restarts
- **Kafka Data**: Kafka data is NOT persisted in a volume by default (for development simplicity)
- **Network**: All services communicate via a dedicated Docker network (`kafka-tooling-network`)
- **Resource Usage**: This stack uses ~2-3GB of RAM. Adjust Docker Desktop memory if needed.

## 🎯 Next Steps

1. Start the environment: `docker compose up -d`
2. Open KCM UI: http://localhost:3000
3. Create a test topic
4. Run the `kafka-broker-tooling` application
5. Use `KafkaBrokerManager` to interact with Kafka programmatically
6. Explore KCM features for development and testing

Happy Kafka development! 🚀

