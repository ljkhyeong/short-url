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