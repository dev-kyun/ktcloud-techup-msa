# step-6-spring-multi-module

Spring Boot 기반 멀티모듈 예제.
멀티모듈 구조로 서비스 단위를 나누고 MSA 관점으로 확장하는 단계.

## Gradle 간단 소개

Gradle은 빌드 자동화와 의존성 관리를 위한 도구.
멀티모듈에서도 공통 설정과 빌드 흐름을 통일할 수 있음.

## 왜 Gradle로 멀티모듈을 구성하나

- 공통 코드 재사용을 구조적으로 관리
- 모듈 간 의존 관계를 명시적으로 선언
- 빌드/테스트/배포 흐름을 한 번에 통일
- 버전/의존성 관리 중앙화

## 구조

```
step-6-spring-multi-module/
├── common/          # 공통 모듈
├── app-service/     # 서비스 A
├── admin-service/   # 서비스 B
└── docker/          # Docker 설정
```

## 모듈 관계

- `app-service` → `common`
- `admin-service` → `common`

## 필수 구성요소

- `settings.gradle`: 모듈 등록 (`include`)
- 루트 `build.gradle`: 공통 설정(버전, 저장소)
- 모듈 `build.gradle`: 역할별 플러그인/의존성
- Gradle Wrapper: 팀/CI 동일 버전 보장

## 빌드

```bash
# build: 전체 빌드
./gradlew build
```

모듈별 빌드:
```bash
# :app-service:bootJar: app-service 부트 JAR 생성
./gradlew :app-service:bootJar
# :admin-service:bootJar: admin-service 부트 JAR 생성
./gradlew :admin-service:bootJar
```

## 실행

```bash
# :app-service:bootRun: app-service 실행
./gradlew :app-service:bootRun
# :admin-service:bootRun: admin-service 실행
./gradlew :admin-service:bootRun
```

요청 예시:
```bash
# curl: HTTP 요청 전송
# app-service
curl http://localhost:8081/app/hello
# admin-service
curl http://localhost:8082/admin/hello
```

예상 응답:
```json
{"message":"[APP] 홍길동"}
```

```json
{"message":"[ADMIN] 홍길동"}
```

## Docker 실행

Dockerfile은 이미지(기반 OS/런타임 + 앱 + 실행 명령)를 정의하는 파일.
필수 요소: `FROM`(기반), `COPY/ADD`(앱 포함), `ENTRYPOINT/CMD`(실행).
자주 사용: `WORKDIR`, `EXPOSE`, `ENV`, `RUN`.
예시: `WORKDIR /app`, `EXPOSE 8081`, `ENV SPRING_PROFILES_ACTIVE=prod`, `RUN apt-get update && apt-get install -y curl`.
JDK 도구 예: `javac`, `jar`, `javadoc`, `jlink`.
회사 정책/운영 환경에 따라 베이스 OS를 선택할 수 있음.
docker-compose는 여러 컨테이너를 함께 실행/관리하는 설정 파일.
예시: `docker build -t app .`, `docker run -p 8081:8081 app`, `docker compose up -d`.
/app은 앱 전용 작업 디렉토리로 파일을 한 곳에 모아 관리하기 쉬움.
`WORKDIR /app`는 이후 명령이 `/app`을 기준으로 동작하도록 설정.
자세한 Docker 가이드는 `DOCKER_GUIDE.md` 참고.

```bash
# docker compose: 여러 서비스 동시 실행
# --build: 이미지가 없으면 빌드
docker compose up --build
```

컨테이너 내부 파일 구조 확인 예시:
```bash
# 실행 중인 컨테이너 확인
docker ps

# 컨테이너 내부 쉘 접속 (sh가 없으면 bash)
docker exec -it <컨테이너명> sh

# 컨테이너 안에서 경로 이동 후 파일 확인
cd /app
ls -la
```

Docker 로그 확인 예시:
```bash
# 로그 확인
docker logs <컨테이너명>

# 실시간 로그 확인
docker logs -f <컨테이너명>

# 최근 100줄만
docker logs --tail 100 <컨테이너명>
```

## build-logs

`build-logs/`에 빌드/실행 로그 보관.

## MSA 관점

모듈 단위 빌드/배포가 가능하다는 점이 MSA 전환의 기반.
자세한 내용은 `MSA_GUIDE.md` 참고.

## 참고

- `.idea`: IntelliJ 설정 폴더, 빌드/실행 필수 아님, 보통 Git 제외
- `.gradle`: Gradle 캐시/메타데이터, 빌드 속도용, 보통 Git 제외
