# 🏥 Borderless Hospital Appointment System — Docker Exercise

## Borderless Tech Academy — DevOps Class

---

## 📋 Overview

You are given a fully working three-tier web application (frontend, backend, database). Your job is to containerize it by writing all the Docker configuration files from scratch.

Application Stack:

| Layer | Technology | Folder |
|---|---|---|
| Presentation | React (served by Nginx) | `frontend/` |
| Application | Java 17 + Spring Boot 3.2 | `backend/` |
| Data | PostgreSQL 15 | _(Docker image)_ |

**What you will create:**

1. `backend/Dockerfile` — Multi-stage build for the Spring Boot API
2. `frontend/Dockerfile` — Multi-stage build for the React app
3. `frontend/nginx.conf` — Nginx configuration for serving React and proxying API calls
4. `docker-compose.yml` — Orchestrates all 3 services together

---

## 🎯 Learning Objectives

By completing this exercise, you will:

- Understand multi-stage Docker builds and why they matter
- Write Dockerfiles for Java/Maven and Node.js/React applications
- Configure Nginx as a reverse proxy
- Use Docker Compose to orchestrate a multi-container application
- Understand Docker networking (service name DNS resolution)
- Implement health checks and service dependencies
- Use named volumes for data persistence

---

## 📖 Prerequisites

- Docker and Docker Compose installed on your machine
- Basic understanding of Docker concepts (images, containers, volumes, networks)
- Git installed

---

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Compose                        │
│                                                         │
│  ┌─────────────┐   ┌──────────────┐   ┌─────────────┐  │
│  │  Frontend    │   │   Backend    │   │  PostgreSQL  │  │
│  │  (Nginx)     │──▶│ (Spring Boot)│──▶│  Database    │  │
│  │  Port: 3000  │   │  Port: 8080  │   │  Port: 5432  │  │
│  └─────────────┘   └──────────────┘   └─────────────┘  │
│                                                         │
│              hospital_network (bridge)                   │
└─────────────────────────────────────────────────────────┘
```

**Request Flow:**
1. User opens browser at `http://localhost:3000`
2. Nginx serves the React static files
3. React makes API calls to `/api/*`
4. Nginx proxies `/api/*` requests to `backend:8080`
5. Spring Boot processes the request and queries PostgreSQL
6. Response flows back through the same chain

---

## 📂 Project Structure

```
three-tier-java-springboot-project/
├── backend/
│   ├── src/                    # Java source code (provided)
│   ├── pom.xml                 # Maven dependencies (provided)
│   └── Dockerfile              # ⬅️ YOU CREATE THIS
├── frontend/
│   ├── src/                    # React source code (provided)
│   ├── public/                 # Static assets (provided)
│   ├── package.json            # NPM dependencies (provided)
│   ├── .env.development        # Dev environment config (provided)
│   ├── Dockerfile              # ⬅️ YOU CREATE THIS
│   └── nginx.conf              # ⬅️ YOU CREATE THIS
├── docker-compose.yml          # ⬅️ YOU CREATE THIS
└── exercise.md                 # This file
```

---

## 📝 Step-by-Step Instructions

### Step 1: Understand the Application Configuration

Before writing any Docker files, study these key files:

**`backend/src/main/resources/application.yml`** — The Spring Boot app reads database connection info from environment variables:

| Environment Variable | Purpose | Default (local dev) |
|---|---|---|
| `DB_HOST` | Database hostname | `localhost` |
| `DB_PORT` | Database port | `5432` |
| `DB_NAME` | Database name | `hospitaldb` |
| `DB_USER` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `SERVER_PORT` | App listening port | `8080` |

**`frontend/src/services/api.js`** — The React app uses `REACT_APP_API_URL` to know where the backend is. When empty (in Docker), it uses relative URLs like `/api/*`, which Nginx will proxy.

**`backend/pom.xml`** — Maven project using Java 17 and Spring Boot 3.2. The build command is `mvn clean package -DskipTests`.

**`frontend/package.json`** — React app. Build command is `npm run build`, which outputs static files to a `build/` directory.

> 💡 **Key Insight:** Inside Docker Compose, containers talk to each other using **service names** as hostnames. If your database service is named `postgres`, the backend connects to it using `postgres` as the hostname — NOT `localhost`.

---

### Step 2: Create the Backend Dockerfile (`backend/Dockerfile`)

Create a **multi-stage** Dockerfile with two stages:

**Stage 1 — Build (use `maven:3.9.5-eclipse-temurin-17` as base image):**
1. Set the working directory to `/app`
2. Copy `pom.xml` first and run `mvn dependency:go-offline -B` to cache dependencies
3. Copy the `src` directory
4. Run `mvn clean package -DskipTests` to build the JAR

**Stage 2 — Runtime (use `eclipse-temurin:17-jre-alpine` as base image):**
1. Set the working directory to `/app`
2. Copy the built JAR from Stage 1 (`/app/target/*.jar`) and name it `app.jar`
3. Expose port `8080`
4. Set the entrypoint to run the JAR: `java -jar app.jar`

> 💡 **Why multi-stage?** Stage 1 includes Maven + full JDK (~600MB). Stage 2 only has the JRE (~200MB). The final image is much smaller because the build tools are discarded.

> 💡 **Why copy pom.xml first?** Docker caches layers. If `pom.xml` hasn't changed, Docker reuses the cached dependency layer and skips the slow download step on subsequent builds.

**Hints:**
```dockerfile
# Stage 1 keywords: FROM ... AS builder, WORKDIR, COPY, RUN
# Stage 2 keywords: FROM, WORKDIR, COPY --from=builder, EXPOSE, ENTRYPOINT
```

---

### Step 3: Create the Nginx Configuration (`frontend/nginx.conf`)

Create an Nginx config file that does two things:

1. **Serves the React static files** from `/usr/share/nginx/html`
2. **Proxies API requests** (`/api/*`) to the backend service

**Requirements:**
- Listen on port `80`
- Set `server_name` to `localhost`
- Set `root` to `/usr/share/nginx/html` and `index` to `index.html`
- For the `/` location: use `try_files $uri $uri/ /index.html` — this is critical for React Router (client-side routing) to work. Without it, refreshing a page like `/patient-dashboard` would return a 404.
- For the `/api/` location: proxy requests to `http://backend:8080` and forward these headers:
  - `Host` → `$host`
  - `X-Real-IP` → `$remote_addr`
  - `X-Forwarded-For` → `$proxy_add_x_forwarded_for`
  - `X-User-Id` → `$http_x_user_id` (custom auth header used by the app)

> 💡 **Why `try_files $uri $uri/ /index.html`?** React is a Single Page Application (SPA). All routing happens in the browser via JavaScript. When a user refreshes on `/patient-dashboard`, Nginx must serve `index.html` so React Router can handle the route — there is no actual file called `patient-dashboard`.

> 💡 **Why `backend` as hostname?** Inside Docker Compose, service names are resolvable as DNS hostnames. The service named `backend` in your `docker-compose.yml` is reachable at `http://backend:8080`.

**Skeleton:**
```nginx
server {
    listen ___;
    server_name ___;

    root ___;
    index ___;

    location / {
        try_files ___ ___ ___;
    }

    location /api/ {
        proxy_pass ___;
        proxy_set_header Host ___;
        proxy_set_header X-Real-IP ___;
        proxy_set_header X-Forwarded-For ___;
        proxy_set_header X-User-Id ___;
    }
}
```

---

### Step 4: Create the Frontend Dockerfile (`frontend/Dockerfile`)

Create another **multi-stage** Dockerfile:

**Stage 1 — Build (use `node:18-alpine` as base image):**
1. Set the working directory to `/app`
2. Copy `package.json` first and run `npm install` to cache dependencies
3. Copy all remaining files (`.`)
4. Run `npm run build` to produce the production build

**Stage 2 — Serve (use `nginx:alpine` as base image):**
1. Copy the build output from Stage 1 (`/app/build`) to `/usr/share/nginx/html`
2. Copy your `nginx.conf` to `/etc/nginx/conf.d/default.conf`
3. Expose port `80`
4. Set the CMD to: `nginx -g "daemon off;"`

> 💡 **Why Nginx instead of Node in production?** React's dev server (`npm start`) is for development only — it's slow and not secure. Nginx is a high-performance static file server designed for production.

> 💡 **Why `daemon off`?** Docker expects the main process to run in the foreground. By default, Nginx runs as a daemon (background process), which would cause the container to exit immediately.

---

### Step 5: Create the Docker Compose File (`docker-compose.yml`)

Create a `docker-compose.yml` (version `3.8`) that defines **3 services**, a **named volume**, and a **custom network**.

#### Service 1: `postgres` (Data Layer)
- **Image:** `postgres:15-alpine`
- **Container name:** `hospital_postgres`
- **Environment variables:**
  - `POSTGRES_DB`: `hospitaldb`
  - `POSTGRES_USER`: `postgres`
  - `POSTGRES_PASSWORD`: `postgres123`
- **Volume:** Mount a named volume `postgres_data` to `/var/lib/postgresql/data`
- **Port:** Map `5432:5432`
- **Network:** `hospital_network`
- **Health check:** Run `pg_isready -U postgres` every 10 seconds, timeout 5s, 5 retries

#### Service 2: `backend` (Application Layer)
- **Build:** Context is `./backend`, Dockerfile is `Dockerfile`
- **Container name:** `hospital_backend`
- **Environment variables:**
  - `DB_HOST`: `postgres` ← this is the **service name**, not localhost!
  - `DB_PORT`: `5432`
  - `DB_NAME`: `hospitaldb`
  - `DB_USER`: `postgres`
  - `DB_PASSWORD`: `postgres123`
  - `SERVER_PORT`: `8080`
- **Port:** Map `8080:8080`
- **Depends on:** `postgres` with condition `service_healthy`
- **Network:** `hospital_network`

#### Service 3: `frontend` (Presentation Layer)
- **Build:** Context is `./frontend`, Dockerfile is `Dockerfile`
- **Container name:** `hospital_frontend`
- **Port:** Map `3000:80` (host 3000 → Nginx 80 inside container)
- **Depends on:** `backend`
- **Network:** `hospital_network`

#### Volume:
- `postgres_data` with `local` driver

#### Network:
- `hospital_network` with `bridge` driver

> 💡 **Why a health check on postgres?** Spring Boot will crash if it tries to connect before PostgreSQL is ready. The `depends_on` with `condition: service_healthy` ensures the backend only starts after PostgreSQL passes the health check.

> 💡 **Why a named volume?** Without it, all database data is lost when the container is removed. Named volumes persist data across container restarts and removals.

> 💡 **Why a custom network?** All services on the same Docker network can resolve each other by service name. The backend can reach the database at hostname `postgres` because they share `hospital_network`.

---

### Step 6: Build and Run

Open a terminal in the project root directory and run:

```bash
# Build all images and start all containers
docker-compose up --build
```

Watch the logs. You should see:
1. PostgreSQL starting and becoming ready
2. Backend starting (after postgres health check passes) and connecting to the database
3. Frontend (Nginx) starting

Once everything is up, open your browser:
- **Frontend:** http://localhost:3000
- **Backend API (direct):** http://localhost:8080/api/doctors

---

### Step 7: Test the Application

1. Go to http://localhost:3000
2. Register a new patient account
3. Log in with the patient credentials
4. View the patient dashboard
5. Try booking an appointment

---

### Step 8: Verify Your Understanding

Run these commands and make sure you understand the output:

```bash
# List running containers
docker ps

# View the custom network
docker network ls
docker network inspect hospital_postgres  # (or your network name)

# Check the volume
docker volume ls

# View logs for a specific service
docker-compose logs backend
docker-compose logs postgres

# Stop everything
docker-compose down

# Stop everything AND delete the database volume
docker-compose down -v
```

---

## ✅ Acceptance Criteria

Your submission is complete when:

- [ ] `backend/Dockerfile` exists and uses a multi-stage build (Maven → JRE)
- [ ] `frontend/Dockerfile` exists and uses a multi-stage build (Node → Nginx)
- [ ] `frontend/nginx.conf` exists and correctly proxies `/api/` to the backend
- [ ] `docker-compose.yml` exists and defines all 3 services, a volume, and a network
- [ ] `docker-compose up --build` starts all services without errors
- [ ] http://localhost:3000 loads the React frontend
- [ ] You can register, log in, and book an appointment
- [ ] Stopping and restarting (`docker-compose down` then `docker-compose up`) preserves database data
- [ ] `docker-compose down -v` removes the database data (clean slate)

---

## 🚨 Common Mistakes to Avoid

| Mistake | Why It Fails |
|---|---|
| Using `localhost` as `DB_HOST` in docker-compose | Containers have their own network. Use the **service name** (`postgres`) |
| Forgetting `try_files` in nginx.conf | React Router pages will 404 on browser refresh |
| Not using multi-stage builds | Final images will be unnecessarily large (600MB+ instead of ~200MB) |
| Forgetting `daemon off` in Nginx CMD | Container exits immediately because Nginx runs in background |
| Not setting `depends_on` with health check | Backend crashes because PostgreSQL isn't ready yet |
| Copying `node_modules` into Docker | Bloats the image. Let `npm install` run inside the container |
| Forgetting to expose ports | Services run but are unreachable from the host |

---

## 📚 Useful References

- [Dockerfile Reference](https://docs.docker.com/engine/reference/builder/)
- [Docker Compose File Reference](https://docs.docker.com/compose/compose-file/)
- [Nginx Reverse Proxy Guide](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
- [Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)

---

## 🎁 Bonus Challenges (Optional)

1. **Add a `.dockerignore`** file to both `backend/` and `frontend/` to exclude unnecessary files from the build context (e.g., `node_modules`, `target`, `.git`)
2. **Add a `restart: unless-stopped`** policy to all services so they auto-restart on failure
3. **Use build arguments (`ARG`)** to make the Java version configurable in the backend Dockerfile
4. **Add a pgAdmin service** to docker-compose for a web-based database management UI

---

Created by Borderless Tech Academy - April 2026! 
