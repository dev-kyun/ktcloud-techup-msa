# step-6-spring-multi-module

Spring Boot 기반 멀티모듈 예제.
멀티모듈 구조로 서비스 단위를 나누고 MSA 관점으로 확장하는 단계.


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


## MSA 관점

모듈 단위 빌드/배포가 가능하다는 점이 MSA 전환의 기반.
자세한 내용은 `MSA_GUIDE.md` 참고.

## 참고

- `.idea`: IntelliJ 설정 폴더, 빌드/실행 필수 아님, 보통 Git 제외
- `.gradle`: Gradle 캐시/메타데이터, 빌드 속도용, 보통 Git 제외
