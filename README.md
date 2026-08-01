# Banking Microservices — End-to-End Reference Project

Ye ek **poora runnable Spring Boot microservices project** hai jisme humari saari
notebooks ke concepts practically implement kiye gaye hain — ek real banking
system (Account &rarr; Transfer &rarr; Notification) ke through.

## Architecture

```
Client
  │
  ▼
API Gateway (8080)  ──── JWT auth, rate limiting, routing
  │
  ├──► Account Service (8081)  ──► Postgres (account_db) + Redis (cache)
  │
  └──► Transaction Service (8082) ──► Postgres (transaction_db)
           │  (calls Account Service via Circuit Breaker)
           │
           └──► Kafka (transaction-events topic)
                    │
                    ▼
              Notification Service (8083) ──► H2 (in-memory)

Eureka Server (8761)   — sab services yahan register hoti hain
Config Server (8888)   — business config yahan se serve hota hai
```

## Concept-to-Code Mapping (saari notebooks yahan hain)

| Notebook | Kahan implement hua hai |
|---|---|
| **API Gateway** | `api-gateway/` — routing, JWT filter, login |
| **Microservices Architecture** | Poora project — database-per-service, 6 independent services |
| **ACID Transactions** | `AccountService.debit()/credit()` — `@Transactional` + pessimistic lock |
| **Service Discovery** | `eureka-server/` — sab services isse register hoti hain |
| **Config Server** | `config-server/` — `config-repo/*.yml` files |
| **Circuit Breaker** | `transaction-service/client/AccountServiceClient.java` — Resilience4j |
| **Load Balancing & Rate Limiting** | `api-gateway` (Redis rate limiter), `@LoadBalanced RestTemplate` |
| **Message Broker** | `transaction-service/messaging/` (producer) + `notification-service/messaging/` (consumer) |
| **Observability & Health Checks** | `DatabaseHealthIndicator.java`, Actuator endpoints, K8s probes |
| **Container Orchestration** | `k8s/*.yaml` — Deployment, Service, HPA, PDB |
| **Saga, CQRS & Event Sourcing** | `TransferSagaOrchestrator.java` — poora orchestration-based saga + compensating transaction |
| **Security Deep-Dive** | JWT generation/validation in `api-gateway` |
| **Docker + Kubernetes** | `Dockerfile` (har service mein), `k8s/` folder |
| **Kubernetes + CI/CD** | `k8s/*.yaml` manifests (CI/CD pipeline khud isme included nahi, docs mein mention hai) |
| **Database Performance & Caching** | `account-service` — `@Cacheable`, Redis, `CacheConfig.java` |
| **Consistent Hashing / Distributed Locks / CAP** | Pessimistic locking in `AccountRepository.findByIdForUpdate()` |
| **SQL vs NoSQL** | Postgres (account/transaction) + H2 for notification (polyglot persistence demo) |
| **API Design** | REST conventions across all controllers — proper HTTP verbs, status codes, pagination |
| **Real-time Systems** | (Not included — would be a WebSocket notification channel, left as an exercise) |
| **Testing Strategies** | `AccountServiceTest.java`, `TransferSagaOrchestratorTest.java` — Mockito unit tests |
| **Spring Boot Backend (all sub-topics)** | Entity/Repository/Service/Controller/DTO/Exception/Validation everywhere |
| **Collections & Streams** | `AccountService` — `Collectors.toList()`, `reduce()`, `ConcurrentHashMap` |
| **Multithreading** | `FraudCheckService.java` — `CompletableFuture.supplyAsync()`, `thenCombine()` |

## How to Run

### Prerequisites
- Docker + Docker Compose installed
- (Optional, for local dev without Docker) Java 17, Maven 3.9+

### Option 1 — Run everything with Docker Compose (recommended)

```bash
cd banking-microservices
docker-compose up --build
```

Ye command:
1. Postgres (dono databases banayega `init-db.sql` se), Redis, Kafka+Zookeeper start karega
2. Eureka Server (8761), Config Server (8888) start karega
3. API Gateway (8080) start karega
4. Account, Transaction, Notification services start karenge

Startup mein 2-3 minute lag sakte hain (sab services ek doosre ka wait karti hain).

### Option 2 — Run locally without Docker (development mode)

1. Postgres, Redis, Kafka apne machine pe alag se install/run karo (ya sirf infra ke liye docker-compose use karo: `docker-compose up postgres redis kafka zookeeper`)
2. Har module mein: `mvn spring-boot:run` (Eureka Server se shuru karo, phir Config Server, phir baaki)

## Try the End-to-End Flow

```bash
# 1. Login (JWT token milega)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
# Response: { "token": "eyJhbGci..." }

# 2. Create a customer (directly account-service pe, ya gateway se agar route add karo)
curl -X POST http://localhost:8081/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","username":"alice","email":"alice@bank.com"}'

# 3. Create 2 accounts for that customer
curl -X POST http://localhost:8080/api/accounts \
  -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
  -d '{"customerId":1,"accountType":"SAVINGS"}'

curl -X POST http://localhost:8080/api/accounts \
  -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
  -d '{"customerId":1,"accountType":"CURRENT"}'

# 4. Manually credit account 1 (test data ke liye)
curl -X POST http://localhost:8081/api/accounts/1/credit \
  -H "Content-Type: application/json" -d '{"amount": 5000}'

# 5. Transfer money — poora Saga chalega yahan!
curl -X POST http://localhost:8080/api/transfers \
  -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
  -d '{"fromAccountId":1,"toAccountId":2,"amount":500}'

# 6. Check notifications (Kafka consumer ne process kiya hoga)
curl http://localhost:8083/api/notifications/account/1

# 7. Dekh lo Eureka dashboard mein sab services registered hain
open http://localhost:8761
```

## Testing the Circuit Breaker

Account Service ko stop kar do (`docker-compose stop account-service`), phir
transfer try karo — Circuit Breaker kuch failures ke baad "Open" ho jaayega
aur turant 503 error dega bina retry kiye. Ye check karo:
```bash
curl http://localhost:8082/actuator/health
# "circuitBreakers" section mein "accountService" ka state dikhega: CLOSED/OPEN/HALF_OPEN
```

## Testing the Saga Compensation

Account 2 ko FROZEN status pe set karo (DB mein directly, ya ek admin
endpoint banao), phir transfer karo — debit ho jaayega account 1 se, lekin
credit account 2 ko fail hoga (kyunki frozen hai) — dekhoge ki
compensating transaction chalti hai aur account 1 ka paisa wapas aata hai.

## Project Structure

```
banking-microservices/
├── pom.xml                          # Parent multi-module pom
├── docker-compose.yml               # Poora system ek command mein
├── init-db.sql                      # Postgres databases banata hai
├── eureka-server/                   # Service Discovery
├── config-server/                   # Centralized config (+ config-repo/)
├── api-gateway/                     # Entry point, JWT, rate limiting
├── account-service/                 # Customer + Account management
├── transaction-service/             # Saga orchestrator, Circuit Breaker
├── notification-service/            # Kafka consumer
└── k8s/                             # Kubernetes manifests (Deployment, Service, HPA, PDB)
```

## Known Simplifications (production mein alag hote)

- JWT secret hardcoded hai `application.yml` mein — production mein Vault/Secrets Manager use karo
- Login demo ke liye hardcoded users hai — real system mein DB + BCrypt chahiye
- Notification Service H2 (in-memory) use karta hai — restart pe data chala jaata hai
- Config Server "native" profile use kar raha hai (local files) — production mein Git-backed
- Koi distributed tracing (Zipkin/Jaeger) setup nahi hai — "Observability" notebook mein cover kiya gaya concept hai, yahan add nahi kiya scope control ke liye
