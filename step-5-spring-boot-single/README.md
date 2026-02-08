# step-5-spring-boot-single


## 학습 목표
- Spring Boot 단일 모듈 프로젝트 구조 이해
- bootRun과 일반 run의 차이 이해
- Docker로 Spring Boot 앱 실행하는 방법 익히기

## 기본 구성
- Spring Boot 단일 모듈 기본 예제.  
- 같은 앱 안에 `HelloController`, `AdminHelloController`를 함께 둔 상태.  
- step-6에서 멀티모듈로 분리하는 흐름을 보여주는 기준 예제.
- 단일 모듈 안에 `/app/hello`, `/admin/hello`가 함께 존재
- 다음 단계에서 모듈 분리로 책임을 나눔

## Gradle 명령어 실행 예시
```bash
# clean: 빌드 경로 초기화
./gradlew clean
```

```bash
# build: 전체 빌드
./gradlew build
```


```bash
# bootRun: Spring Boot 실행
./gradlew bootRun
```


## API 요청 예시
```bash
# curl: HTTP 요청 전송
[요청]
curl http://localhost:8080/app/hello
curl http://localhost:8080/admin/hello

[응답]
{"message":"[APP] 홍길동"}
{"message":"[ADMIN] 홍길동"}
```

## 프로젝트 구조
```aiexclude
step-5-spring-boot-single/
├── build.gradle
├── settings.gradle
├── docker
    └── Dockerfile
├── docker-compose.yml
└── src/
    └── main/
    ├── java/
    │   └── com/kyun/boot/
    │       ├── BootSingleApplication.java   ← @SpringBootApplication
    │       ├── app/
    │       │   └── HelloController.java     ← /app/hello
    │       └── admin/
│               └── AdminHelloController.java ← /admin/hello
└── resources/
└── application.yml
```

### 현재 문제점
```aiexclude
하나의 모듈에 app과 admin이 함께 존재
├── app/HelloController.java      ← 사용자용
└── admin/AdminHelloController.java  ← 관리자용

- 코드가 섞여 있어 분리 배포 불가
- step-6에서 멀티모듈로 분리
```


##  일반 Gradle 프로젝트 vs Spring Boot 프로젝트

| 항목 | 일반 Gradle (step-3) | Spring Boot (step-5) |
|------|---------------------|----------------------|
| 플러그인 | `java`, `application` | `org.springframework.boot` |
| 실행 Task | `./gradlew run` | `./gradlew bootRun` |
| JAR Task | `./gradlew jar` | `./gradlew bootJar` |
| Main 클래스 | `application.mainClass` | `@SpringBootApplication` 자동 탐색 |

```gradle
## build.gradle 비교
// 일반 Gradle (step-3)
plugins {
    id 'java'
    id 'application'
}

// Spring Boot (step-5)
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.0'
}
```

## 다음 단계

step-6에서 `app-service`, `admin-service`로 분리.
