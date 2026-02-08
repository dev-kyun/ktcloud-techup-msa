# step-4-multi-module


## 학습 목표
Gradle 멀티모듈 구성을 이해하는 단계.
공통 모듈을 분리하고 실행 모듈을 역할별로 나누는 흐름.

- 멀티모듈이 왜 필요성 이해
- 모듈 간 의존 관계 설정 방법 이해
- 공통 모듈과 실행 모듈의 역할 구분 이해

## Gradle 소개
Gradle은 빌드 자동화와 의존성 관리를 위한 도구.  
멀티모듈에서도 공통 설정과 빌드/실행 흐름을 통일할 수 있음.
> Gradle에 대한 자세한 내용은 step-3의 README 참고.  
> 이 단계에서는 멀티모듈 구성에 집중.

## 구조
```aiexclude
step-4-multi-module/
├── settings.gradle      ← 모듈 등록
├── build.gradle         ← 공통 설정
├── app/                 ← 실행 모듈 1 (사용자용)
│   └── build.gradle
├── admin/               ← 실행 모듈 2 (관리자용)
│   └── build.gradle
├── core/                ← 공통 비즈니스 로직
│   └── build.gradle
└── util/                ← 공통 유틸리티
└── build.gradle
```

## 모듈 의존 관계
```aiexclude
┌───────────────────────────────────────┐
│           실행 모듈 (application)      │
│  ┌─────────┐          ┌─────────┐    │
│  │   app   │          │  admin  │    │
│  └────┬────┘          └────┬────┘    │
│       │                    │         │
│       └────────┬───────────┘         │
│                ↓                     │
│       ┌────────┴────────┐            │
│       ↓                 ↓            │
│  ┌─────────┐       ┌─────────┐      │
│  │  core   │       │  util   │      │
│  └─────────┘       └─────────┘      │
│        공통 모듈 (java-library)       │
└───────────────────────────────────────┘
- `app` → `core`, `util` 의존
- `admin` → `core`, `util` 의존
- `core`, `util`은 독립적 (서로 의존 없음)
```

## 왜 멀티모듈인가? (싱글모듈과 비교)

### 싱글모듈 (step-3)
```aiexclude
step-3-gradle-build/
└── src/
└── com/kt/example/
├── app/Main.java
├── service/UserService.java
└── repository/UserRepository.java

- 모든 코드가 한 곳에
- 프로젝트 커지면 관리 어려움
- app용/admin용 코드가 섞임
```


### 멀티모듈 (step-4)
```step-4-multi-module/
├── app/      ← 사용자용 실행 코드만
├── admin/    ← 관리자용 실행 코드만
├── core/     ← 공통 비즈니스 로직
└── util/     ← 공통 유틸리티

- 역할별로 코드 분리
- 공통 코드 재사용
- 각 모듈 독립적으로 빌드/테스트 가능
```


## 왜 멀티모듈을 구성하나

- 공통 코드 재사용을 구조적으로 관리
- 모듈 간 의존 관계를 명시적으로 선언
- 빌드/테스트 흐름을 한 번에 통일
- 버전/의존성 관리 중앙화
- 제약을 걸어서 질서를 만든다 ( 코드 격리, 제약 )

## 멀티모듈 핵심 개념

- 모듈 경계로 역할 분리
- 공통 모듈 재사용
- 모듈 의존성 명시로 구조 제약 가능

## 핵심 구성 요소

| 구성 | 역할             | 
|------|----------------|
| `settings.gradle` | 모듈 등록          |
| 루트 `build.gradle` | 전체 모듈 공통 설정    | 
| 모듈 `build.gradle` | 특정 모듈별 역할별 설정 |

## settings.gradle / build.gradle 역할

| 파일 | 위치 | 역할                   |
|------|------|----------------------|
| `settings.gradle` | 루트 | 모듈 목록 정의 (`include`) |
| `build.gradle` | 루트 | 공통 설정 (버전, 저장소)      |
| `build.gradle` | 각 모듈 | 역할 별 플러그인/의존성/실행 설정  |

### settings.gradle

모듈 등록. 여기에 없으면 Gradle이 모듈로 인식하지 않음.

```gradle
// settings.gradle
rootProject.name = 'step-4-multi-module'

include 'app', 'admin', 'core', 'util'
```

### build.gradle
```gradle
// 루트 build.gradle
allprojects {
    group = 'com.kyun'
    version = '1.0.0'
}

subprojects {
    apply plugin: 'java'
    
    repositories { 
        mavenCentral() 
    }
    
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
```

```gradle
// app/build.gradle (실행 모듈)
plugins {
    id 'application'
}

dependencies {
    implementation project(':core')
    implementation project(':util')
}

application {
    mainClass = 'com.kyun.multi.app.MainApp'
}
```
```gradle
// core/build.gradle (라이브러리 모듈)
plugins {
    id 'java-library'
}

// 실행 진입점 없음 - 다른 모듈에서 가져다 씀
```


## allprojects / subprojects

- `allprojects`: 루트 포함 모든 모듈에 공통 설정 적용
- `subprojects`: 하위 모듈에만 공통 설정 적용

예시:
```gradle
allprojects {
    group = 'com.kyun'
}

subprojects {
    repositories { mavenCentral() }
}
```


## 변경 요약

| 항목 | 기존 | 개선 |
|------|------|------|
| 파일 역할 | 글머리 기호 | 표로 정리 |
| settings.gradle | 예시만 | `rootProject.name` 추가 |
| 루트 build.gradle | 간단한 예시 | 전체 구조 예시 |
| 모듈 build.gradle | 없음 | 실행/라이브러리 모듈 비교 |
| allprojects vs subprojects | 설명만 | 표 + 구분 이유 추가 |


## Gradle 명령어 실행 예시


```bash
# projects: 모듈 목록 출력
# 설정 파일 기준으로 멀티모듈 로드
./gradlew projects

[출력]
Root project 'step-4-multi-module'
Project ':admin'
Project ':app'
Project ':core'
Project ':util'
```

```bash
# build: 전체 빌드
# 공통 설정이 적용된 상태로 전체 빌드
./gradlew build

[출력]
BUILD SUCCESSFUL
```


```bash
# :app:run: app 모듈 실행
./gradlew :app:run

[출력]
[APP] *** 홍길동 ***
```

```bash
# :admin:run: admin 모듈 실행
./gradlew :admin:run

[출력]
[ADMIN] *** 관리자: 홍길동 ***
```

```bash
# :app:build: app 모듈만 빌드
./gradlew :app:build
# :admin:build: admin 모듈만 빌드
./gradlew :admin:build
```

```bash
# :app:run: app 모듈 실행
./gradlew :app:run
# :admin:run: admin 모듈 실행
./gradlew :admin:run

[출력]
[APP] *** 홍길동 ***
[ADMIN] *** 관리자: 홍길동 ***
```

## 중간 정리 - 서버 빌드 & 배포 & 실행 과정 생각해보기
- 반복적으로 빌드 -> 실행 ( 패키지 포함 )을 하고 있다.
- 그냥 빌드하고 빌드 결과물 어딘가에 옮겨서 실행시키는 것 뿐.
- 이 과정에서 장비가 필요하면 서버를 직접 구성하거나 클라우드(AWS)를 사용한다.
- 그런데, 살행하려는 패키지는 jvm환경에서 돌아가는 환경. 서버에도 동일한 환경을 구축해야한다.
- 환경 세팅도 직접 구성할 수도 있지만, 편리한 방법으로 컨테이너(도커)를 사용한다. 
- 도커는 실행 환경도 함께 패키징한다.
- 위 과정에서 자동화가 필요하다면 젠킨스, github action 등의 도구들을 많이 활용한다. 
- ( 가끔 직접 구축하기도 한다. 그러고 후회한다. )
- AWS는 이런 과정에서 편리한 기능을 많이 제공해준다. 목적에 맞게 기능을 잘 찾아보고 사용하면 좋다.
- CI/CD 관심있는 사람들은 이런 과정에 관심을 많이 가지고 위에서 다룬 명령어들이 익숙해지면 도움이 많이 될 것.
- 결국 추상화 해서 핵심만 보면 말 그대로 `빌드 & 배포 & 실행`일 뿐이다. 


## 참고

- `.idea`: IntelliJ 설정 폴더, 빌드/실행 필수 아님, 보통 Git 제외
- `.gradle`: Gradle 캐시/메타데이터, 빌드 속도용, 보통 Git 제외
- `build-logs/`: 빌드/실행 로그 보관.
- MSA 확장 관점은 `MULTI_MODULE_TO_MSA.md` 참고.


