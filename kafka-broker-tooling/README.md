# Kafka Broker Tooling

This folder contains **tooling and utilities** to run a local Kafka development environment with Docker Compose.  
**This is not a Maven module** — it only provides scripts and configuration files for local Kafka infrastructure.

---

## What's included

- **docker-compose.yml**: Complete Kafka stack with KCM (Kafka Cluster Manager)
- **Makefile**: Common commands for starting/stopping services
- **docker.ps1**: PowerShell script for Windows users
- **.env.example**: Example environment variables

---

## Services provided

The `docker-compose.yml` file sets up the following services:

| Service | Port | Description |
|---------|------|-------------|
| **Kafka Broker** | 9092 | Apache Kafka broker (KRaft mode - no Zookeeper) |
| **KCM UI** | 3000 | Web interface for Kafka management |
| **KCM API** | 8080 | Backend API for Kafka management |
| **PostgreSQL** | 5432 | Database for KCM |
| **Redis** | 6379 | Cache for KCM |

---

## Prerequisites

- Docker Desktop (Windows/macOS) or Docker Engine + Docker Compose (Linux)
- (Optional) Make (for using the Makefile)
- (Optional) PowerShell 5.1+ (Windows)

---

## Quick Start

### Option 1: Docker Compose (cross-platform)

#### Windows (PowerShell)
```powershell
cd D:\workspace\kafka-streams\kafka-broker-tooling
docker compose up -d
```

#### Linux/macOS (Bash)
```bash
cd kafka-broker-tooling
docker compose up -d
```

### Option 2: PowerShell script (Windows)

```powershell
.\docker.ps1 start
```

### Option 3: Makefile (Linux/macOS or Windows with Make)

```bash
make start
```

---

## Access the services

Once the stack is running:

- **Kafka Broker**: `localhost:9092` (KRaft mode - no Zookeeper required)
- **KCM Web UI**: http://localhost:3000
- **KCM API**: http://localhost:8080
- **PostgreSQL**: `localhost:5432` (user: `kcm_user`, password: `kcm_password`, database: `kcm`)
- **Redis**: `localhost:6379`

---

## Using KCM (Kafka Cluster Manager)

**KCM** is a powerful web UI for managing Kafka clusters. It's included in this tooling stack.

🔗 **GitHub repository**: [kcmhub/KCM](https://github.com/kcmhub/KCM.git)

### Features
- Create, view, and manage topics
- Produce and consume messages
- Monitor consumer groups and lag
- View broker and partition information
- Manage schemas (if using Schema Registry)

### Getting started with KCM
1. Start the environment (see Quick Start above)
2. Open http://localhost:3000 in your browser
3. The Kafka cluster connection should be pre-configured
4. Start creating topics, producing messages, etc.

---

## Common operations

### Check service status

```bash
docker compose ps
```

### View logs

```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f kafka
docker compose logs -f kcm-ui
docker compose logs -f kcm-api
```

### Stop the environment

```bash
# Stop containers (data is preserved)
docker compose stop

# Stop and remove containers (data is preserved in volumes)
docker compose down

# Stop, remove containers AND volumes (clean slate — all data lost)
docker compose down -v
```

### Restart a specific service

```bash
docker compose restart kafka
docker compose restart kcm-api
```

---

## Environment variables

You can customize the stack by setting environment variables. Copy `.env.example` to `.env` and edit:

```bash
cp .env.example .env
```

### Example environment variables

#### Windows (PowerShell)
```powershell
$env:KAFKA_BROKER_PORT = "9093"
$env:POSTGRES_PASSWORD = "mysecretpassword"
```

#### Linux/macOS (Bash)
```bash
export KAFKA_BROKER_PORT=9093
export POSTGRES_PASSWORD=mysecretpassword
```

Then restart the services to apply changes:
```bash
docker compose down
docker compose up -d
```

---

## Troubleshooting

### Kafka not starting
- Check if port 9092 is already in use: `netstat -an | findstr 9092` (Windows) or `lsof -i :9092` (Linux/macOS)
- Check logs: `docker compose logs kafka`

### KCM UI not accessible
- Ensure all services are running: `docker compose ps`
- Check KCM API logs: `docker compose logs kcm-api`
- Verify PostgreSQL is ready: `docker compose logs postgres`

### Reset everything
```bash
docker compose down -v
docker compose up -d
```

---

## Additional resources

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [KCM GitHub Repository](https://github.com/kcmhub/KCM.git)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

---

## Notes

- This tooling folder is **not a Maven module** and does not contain Java source code.
- For Kafka Streams Java applications, see the `kafka-streams-template` module in the parent directory.
- For creating new Kafka Streams projects, see the `kafka-streams-archetype` module.

