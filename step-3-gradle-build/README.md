# step-3-gradle-build

- Gradle로 빌드와 의존성 관리하는 단계.
- step-2의 수동 빌드 불편함을 줄이는 흐름. (자동화 단계)


## 학습 목표
- Gradle의 이해
- 수동 빌드(step-2)와 비교했을 때, 장점
- 기본적인 Gradle 명령어 사용법 이해


## Gradle 간단 소개
Gradle은 빌드 자동화와 의존성 관리를 위한 도구.
컴파일/패키징/실행을 명령 하나로 처리.



## Gradle Task
Gradle의 작업 단위
- 미리 정의된 Task: build, clean, run, test 등
- 커스텀 Task: 직접 만들 수 있음
- Task 의존성: Task끼리 순서 지정 가능
- 실행 방법: ./gradlew [Task이름]

### 커스텀 Task 예시
```
// 커스텀 Task 정의
tasks.register('hello') {
    doLast {
        println '안녕하세요!'
    }
}

tasks.register('goodbye') {
    dependsOn hello  // hello 먼저 실행
    doLast {
        println '안녕히 가세요!'
    }
}

// 실행 
// gradlew: Wrapper 실행, goodbye Task 실행
./gradlew goodbye

// 출력
> Task :hello
안녕하세요!

> Task :goodbye
안녕히 가세요!
```


## Gradle 기본 명령어

- `./gradlew build` 빌드 ( jar생성, test 실행 포함 )
- `./gradlew clean` 이전 산출물 삭제
- `./gradlew run` 실행
- `./gradlew test` 테스트
- `./gradlew jar` JAR 생성

## gradle vs gradlew
  | 구분 | `gradle` | `gradlew` |
  |------|----------|-----------|
  | 설치 | 시스템에 직접 설치 필요 | 설치 불필요 |
  | 버전 | 시스템에 설치된 버전 | 프로젝트가 지정한 버전 |
  | 팀 협업 | 버전 불일치 위험 | 모두 동일 버전 보장 |
  | 권장 | ❌ | ✅ |

## 자주 쓰는 Gradle 구성 옵션

> 💡 **일단 이것부터 이해할 것**  
> `plugins`, `dependencies`, `application`, `repositories`    
> 나머지는 필요할 때 마다 조금 씩 찾아볼 것.

### plugins
빌드 기능을 추가하는 플러그인 목록. ( 관련된 task를 제공 )
```gradle
plugins {
    id 'java'          // java 컴파일/테스트 Task 제공( compileJava, jar, test, build )
    id 'application'   // 실행 가능한 애플리케이션 Task 제공 ( run, installDist, distZip )
    # org.springframework.boot // Spring Boot 프로젝트 Task 제공 ( bootJar, bootRun )
}

# java 플러그인 없으면 아래 명령어 실패.
./gradlew build  # ❌ Task 'build' not found
```

### repositories
의존성 다운로드 저장소 지정.
```gradle
repositories {
    mavenCentral() // 많이 사용함.
    # google() 
}
```

### dependencies
외부 라이브러리 의존성 선언.  
스코프(implementation, compileOnly, runtimeOnly, testImplementation, ...)에 따라 포함 범위가 달라짐.
```gradle
dependencies {
    // 일반 라이브러리 (컴파일 + 실행 + 테스트)
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    
    // 컴파일에만 필요 (런타임엔 불필요)
    compileOnly 'org.projectlombok:lombok:1.18.30'
    
    // 실행에만 필요 (컴파일엔 불필요)
    runtimeOnly 'com.mysql:mysql-connector-j:8.0.33'
    
    // 테스트에만 필요
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}
```


### application
./gradlew run 실행 시 진입점(Main 클래스) 지정.
```gradle
application {
    mainClass = 'com.kyun.example.app.Main'
}

// 사전 설정 필요.
plugins {
    id 'application'   // 필수: 이 플러그인이 있어야 정상 동작
}

# application 플러그인 없이 동작 실패
./gradlew run  # ❌ Task 'run' not found

[내부 동작]
# ./gradlew run 실행 시 내부적으로:
java -cp build/classes/java/main:의존성들... com.kyun.example.app.Main
                                            ↑
                                   application.mainClass 값
```

### java
소스/타깃 JDK 버전 지정.
```gradle
java {
    sourceCompatibility = JavaVersion.VERSION_21  // 소스 코드 버전 (코드 작성 시, 버전)
    targetCompatibility = JavaVersion.VERSION_21  // 컴파일 결과 버전 (실행환경 버전) 
    // 나눠져 있는 것을 보면 따로도 쓸 수 있게 만든 것 같으나 그냥 같은 값을 쓰는 것을 권장.
}

ex.
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_17
}
// Java 21 문법으로 작성, Java 17 JVM에서 실행
// (21 전용 기능 사용 시 에러)
```

### ext
공통 상수/버전 관리에 사용. ( 관리 편의 )
```gradle
ext {
    springBootVersion = "3.2.0"
    lombokVersion = "1.18.30"
}

[ 활용 예시 ]
dependencies {
    implementation "org.springframework.boot:spring-boot-starter:${springBootVersion}"
    implementation "org.springframework.boot:spring-boot-starter-web:${springBootVersion}"
    implementation "org.springframework.boot:spring-boot-starter-data-jpa:${springBootVersion}"
    compileOnly "org.projectlombok:lombok:${lombokVersion}"
}
```

### allprojects / subprojects
멀티 모듈 공통 설정 묶음.
```gradle
// 루트 + 모든 하위 모듈에 적용
allprojects {
    group = 'com.kyun'
    version = '1.0.0'
}

// 하위 모듈에만 적용 (루트 제외)
subprojects {
    apply plugin: 'java'
    
    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }
}

[ 멀티 모듈 폴더 구조 예시]
my-project/              ← 루트 (allprojects 적용)
├── build.gradle         ← 위 설정이 여기에
├── settings.gradle
├── common/              ← 서브모듈 (subprojects 적용)
│   └── build.gradle
├── api/                 ← 서브모듈 (subprojects 적용)
│   └── build.gradle
└── admin/               ← 서브모듈 (subprojects 적용)
    └── build.gradle
```


### tasks
커스텀 Task 등록, 실행 순서 제어.
```gradle
tasks.register('hello') {
    doLast { println '안녕하세요!' }
}

tasks.register('goodbye') {
    dependsOn hello
    doLast { println '안녕히 가세요!' }
}

[실햄 명령어 예시]
./gradlew goodbye

[출력 예시]
> Task :hello
안녕하세요!

> Task :goodbye
안녕히 가세요!
```

## 전체 흐름 
```
[소스코드]            [Gradle]               [결과물]             [실행]
    │                  │                     │                 │
    │                  │                     │                 │
 Main.java  ──→  ./gradlew build  ──→   myapp.jar  ──→  ./gradlew run
                       │                                       │
                   자동으로:                                   출력:
                   - 컴파일                                   홍길동
                   - 의존성 다운로드                            ...
                   - JAR 패키징        
                   - 테스트 실행
```


## 의존성 관리 예시 (참고)

```gradle
dependencies {
    implementation 'org.apache.commons:commons-lang3:3.14.0'
}

'그룹ID:아티팩트ID:버전'
'org.apache.commons:commons-lang3:3.14.0'
      ↑              ↑           ↑
   조직/회사명      라이브러리명   버전
   
[동작 흐름]
dependencies에 선언
        ↓
repositories에서 검색
        ↓
mavenCentral()에서 다운로드
```

Gradle이 빌드 시 자동으로 다운로드하고 클래스패스에 추가.

## Gradle 명령어 실행 예시

```bash
# clean: 이전 산출물 삭제
./gradlew clean
```


```bash
# Gradle Wrapper로 빌드
# build: 전체 빌드
./gradlew build
# gradlew 최초 실행 시
# gradle-wrapper.jar가 지정된 Gradle 버전 자동 다운로드
# ~/.gradle/wrapper/dists/에 저장
# 이후 실행 시 재사용

빌드 결과 구조

build/
├── classes/          ← 컴파일된 .class 파일
│   └── java/main/
├── libs/             ← 생성된 JAR 파일
│   └── step-3-gradle-build-0.0.1-SNAPSHOT.jar
├── tmp/              ← 임시 파일
└── reports/          ← 테스트 리포트 등
```

```bash
# Gradle run Task로 실행
# run: application 실행
./gradlew run

[출력]
홍길동
# `./gradlew run`은 클래스패스로 실행되므로 MANIFEST 메타정보가 적용되지 않을 수 있음.
# gradlew run과 jar 명령어와는 다르게 MANIFEST.MF를 안 읽어옴. ( 그냥 처리 과정이 다르다고 알아두면 됨. 학습용 예시일 뿐, 무시해도 괜찮음. 잘 사용 안함. )
Implementation-Version: (none)    
```

## IDE 빌드 동작
### IDE vs 터미널 동작 비교

| IDE 동작 | 터미널 명령어 | 설명 |
|----------|---------------|------|
| Build Project | `./gradlew build` | 전체 빌드 |
| Rebuild Project | `./gradlew clean build` | 클린 후 빌드 |
| Run | `./gradlew run` | 애플리케이션 실행 |
| Run Tests | `./gradlew test` | 테스트 실행 |


## step-2 vs step-3 비교

| 항목 | step-2 (수동) | step-3 (Gradle) |
|------|---------------|-----------------|
| 컴파일 | `javac ...` 직접 입력 | `./gradlew build` |
| 패키징 | `jar cvfm ...` 직접 입력 | 자동 |
| 의존성 | 직접 다운로드 & 경로 지정 | `build.gradle`에 선언만 |
| 실행 | `java -cp ... Main` | `./gradlew run` |



## 참고

- `.idea`: IntelliJ 설정 폴더, 빌드/실행 필수 아님, 보통 Git 제외
- `.gradle`: Gradle 캐시/메타데이터, 빌드 속도용, 보통 Git 제외
- `build-logs/`: 빌드/실행 로그 보관.

