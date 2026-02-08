## 문서 개요
step6 내용을 도커로 실행하는 방법을 설명.
도커에 대한 자세한 설명은 step5의 DOCKER_GUIDE를 참고할 것.

## Dockerfile

```aiexclude
# Dockerfile.app
FROM amazoncorretto:21
WORKDIR /app
COPY app-service/build/libs/app-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

# Dockerfile.admin
FROM amazoncorretto:21
WORKDIR /app
COPY admin-service/build/libs/admin-service.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 실행 예시

### 1. JAR 생성

```bash
# app/admin 부트 JAR 생성
./gradlew :app-service:bootJar :admin-service:bootJar
```

```bash
# 생성 위치 확인
step-6-spring-multi-module/
├── app-service/
│   └── build/libs/app-service.jar
├── admin-service/
│   └── build/libs/admin-service.jar
├── docker/
│   ├── Dockerfile.app
│   └── Dockerfile.admin
└── docker-compose.yml
```

### 2. Docker 이미지 빌드

```bash
#  서비스별 이미지 빌드
docker build -t step6-app -f docker/Dockerfile.app .
docker build -t step6-admin -f docker/Dockerfile.admin .
```

### 3. Docker 이미지 실행 (docker 명령어)

```bash
# app-service 실행
docker run -d -p 8081:8081 --name step6-app step6-app

# admin-service 실행
docker run -d -p 8082:8082 --name step6-admin step6-admin
```

## Docker 실행 상태 확인

```bash
# 실행 중인 컨테이너만
docker ps

# 모든 컨테이너 (중지 포함)
docker ps -a

[출력]
CONTAINER ID   IMAGE          COMMAND               CREATED         STATUS         PORTS                    NAMES
c54fb07db988   step6-admin    "java -jar app.jar"   5 seconds ago   Up 2 seconds   0.0.0.0:8082->8082/tcp   step6-admin
b1e7d8bbea95   step6-app      "java -jar app.jar"   8 seconds ago   Up 6 seconds   0.0.0.0:8081->8081/tcp   step6-app
```

## Docker 실행 환경 접속

```bash
# 컨테이너 내부 접속 ( app )
docker exec -it step6-app sh
# 내부 파일 확인
ls -la
# 쉘 종료
exit

# 컨테이너 내부 접속 ( admin )
docker exec -it step6-admin sh
# 내부 파일 확인
ls -la
# 쉘 종료
exit
```

## 로그 확인

```bash
# 실시간 로그
docker logs -f step6-app
docker logs -f step6-admin
```

## Docker 컨테이너 종료/정리

```bash
# 컨테이너 종료
docker stop step6-app step6-admin

# 컨테이너 제거
docker rm step6-app step6-admin
```


## Docker 실행 환경
```
┌──────────────────────────────────────────────────────────────────┐
│  호스트 (내 PC)                                                    │
│                                                                  │
│        localhost:8081              localhost:8082                │
│             │                           │                        │
│             ▼                           ▼                        │
│        ┌─────────┐                 ┌─────────┐                   │
│        │ :8081   │                 │ :8082   │     ◀─ 외부 포트    │
│        └────┬────┘                 └────┬────┘                   │
│             │                           │                        │
│   ──────────┼───────────────────────────┼────── 포트 매핑 ─────    │
│             │                           │                        │
│   ┌─────────┼───────────────────────────┼────────────────────┐   │
│   │         │                           │                    │   │
│   │         │        Docker Engine                           │   │
│   │         │                                                │   │
│   │         ▼                           ▼                    │   │
│   │    ┌─────────┐                 ┌─────────┐               │   │
│   │    │ :8081   │                 │ :8082   │  ◀─ 내부 포트   │   │
│   │    │         │                 │         │               │   │
│   │    │ app     │                 │ admin   │               │   │
│   │    │ service │                 │ service │               │   │
│   │    │         │                 │         │               │   │
│   │    └─────────┘                 └─────────┘               │   │
│   │     step6-app                  step6-admin               │   │
│   │                    컨테이너들                               │   │
│   └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```