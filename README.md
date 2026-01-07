# Short URL Service

간결하고 강력한 단축 URL 서비스입니다. (Spring Boot + JPA + MySQL)

## 🛠 Tech Stack
- **Core:** Java 21, Spring Boot 4.0
- **Database:** MySQL 8.0, Redis (Caching)
- **Infra:** Docker, GitHub Actions (CI)
- **Testing:** JUnit5, TestContainers

## 🚀 How to Run

### 1. Infrastructure (Docker)
이 프로젝트는 **MySQL**과 **Redis**가 필수입니다. Docker Compose를 사용해 한 번에 실행할 수 있습니다.
```bash
docker-compose up -d
```

### 2. Application 
```bash
./gradlew bootRun
```

### 3. API Usage
- 단축URL 생성: POST /api/v1/short-links ({ "url": "..." })
- 이동하기: GET /{shortKey}

## 🧪 How to Test
TestContainers를 사용하여 별도의 DB 설치 없이 테스트가 가능합니다. (Docker 환경 필요)
```bash
./gradlew test
```

## 🚀 Performance & Optimization
단축 URL 서비스의 핵심인 **빠른 리다이렉트 속도**와 **대용량 트래픽 안정성**을 확보하기 위해 아키텍처를 개선했습니다.

### 1. Architecture Evolution
초기에는 단순 DB 조회 방식이었으나, 트래픽 증가 시 **DB Connection Pool 고갈** 및 **쓰기 락**으로 인한 병목 현상이 발생했습니다. 이를 해결하기 위해 **Redis 캐싱**과 **Async**을 도입했습니다.

- **As-Is (Legacy):**
    - 요청 → DB 조회 → DB 조회수 증가(Sync) → 응답
    - 문제점: 조회수 Update 시 DB Lock 발생, 응답 지연 (p95: ~540ms)

- **To-Be (Modern):**
    - 요청 → **Redis Cache 조회** → (Miss시 DB 조회 + 캐싱) → 응답
    - 조회수 증가: **`@Async` + `ThreadPool`**을 사용하여 Non-blocking 처리
    - 개선 효과: **DB 부하 제거 및 응답 속도 획기적 단축**

### 2. Load Testing Results (with k6)
`k6`를 사용하여 VUser 50명, 지속 시간 2분의 부하 테스트를 진행한 결과입니다.

| 측정 지표 (Metric) | 🐢 개선 전 (RDB Only)              | 🚀 개선 후 (Redis + Async) | 📈 개선율 |
| :--- |:--------------------------------| :--- | :--- |
| **p(95) Latency** | **541.99ms**                    | **37.18ms** | **14.5배 속도 향상** |
| **Avg Latency** | 179.87ms                        | 11.25ms | - |
| **Error Rate** | 16.14% (DB Connection Time-out) | **0.00%** (Stable) | **완벽한 안정성 확보** |
| **Throughput** | 150 RPS                         | 243 RPS | 62% 증가 |

> **Test Environment:**
> - Tool: k6 (Load Testing), Grafana & Prometheus (Monitoring)

### 3. Key Tech Decisions
- **Redis (Cache-Aside Pattern):**
    - 읽기 요청이 많은 단축 URL 특성상, 자주 조회되는 Key를 메모리에 캐싱하여 RDB 부하를 최소화했습니다.
- **Spring @Async:**
    - 사용자의 리다이렉트 경험에 영향을 주지 않기 위해, `view_count` 업데이트 로직을 별도 스레드 풀(`ThreadPoolTaskExecutor`)에서 비동기로 처리했습니다.
---
## 🎨 Frontend Features
사용자 편의를 위해 **Thymeleaf**와 **Bootstrap 5**를 기반으로 한 웹 인터페이스를 제공합니다.
- **Simple UI:** 직관적인 URL 입력 폼 제공
- **AJAX Integration:** 페이지 새로고침 없는 비동기 단축 URL 생성
- **UX Enhancement:** 생성된 단축 URL **'원클릭 복사'** 기능
- **Responsive Design:** 모바일/데스크탑 모두 대응 가능한 반응형 웹

---