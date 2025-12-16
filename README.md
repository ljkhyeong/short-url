# Short URL Service

간결하고 강력한 단축 URL 서비스입니다. (Spring Boot + JPA + MySQL)

## 🛠 Tech Stack
- Java 21, Spring Boot 3.x
- JPA, MySQL 8.0 (Docker)
- Base62 Encoding

## 🚀 How to Run

### 1. Infrastructure (Docker)
로컬 개발 환경을 위해 DB를 실행합니다.
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