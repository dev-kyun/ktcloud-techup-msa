## Docker란?
애플리케이션을 컨테이너로 패키징하여 어디서든 동일하게 실행할 수 있게 해주는 도구.

```aiexclude
개발 환경 (내 PC)     →     운영 환경 (서버)
    │                         │
    └─── 같은 Docker 이미지 ────┘
               │
           동일하게 실행
```

## Dockerfile

이미지를 정의하는 파일. "어떤 환경에서, 무엇을, 어떻게 실행할지" 작성.

```aiexclude
# 1. 기반 이미지 (OS + 런타임)
FROM amazoncorretto:21

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. JAR 파일 복사
COPY build/libs/*.jar app.jar

# 4. 포트 문서화
EXPOSE 8080

# 5. 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Dockerfile 주요 명령어

| 명령어 | 역할                                                                        | 필수 | 예시 |
|--------|---------------------------------------------------------------------------|------|------|
| `FROM` | 기반 이미지 지정                                                                 | ✅ | `FROM eclipse-temurin:21-jre` |
| `COPY` | 파일 복사                                                                     | ✅ | `COPY build/libs/*.jar app.jar` |
| `ENTRYPOINT` | 실행 명령                                                                     | ✅ | `ENTRYPOINT ["java", "-jar", "app.jar"]` |
| `WORKDIR` | 작업 디렉토리                                                                   | 권장 | `WORKDIR /app` |
| `EXPOSE` | 포트 설정 (실제로 호스트로 노출되거나 연결되는 옵션은 아님. 문서상 안내 역할. 이 포트를 쓸 사용할 예정이니 참고하라는 정도 ) | 권장 | `EXPOSE 8080` |
| `ENV` | 환경 변수 설정                                                                  | 선택 | `ENV SPRING_PROFILES_ACTIVE=prod` |
| `RUN` | 빌드 시 명령 실행                                                                | 선택 | `RUN apt-get update` |

> 💡 `WORKDIR /app`을 설정하면 이후 명령이 `/app` 기준으로 동작


### docker-compose

여러 컨테이너를 함께 실행/관리하는 설정 파일.

```yaml
# docker-compose.yml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```


---

### 실행 예시

#### 1. Docker 이미지 빌드

```bash
# 도커 파일 작성 (Dockerfile)
FROM amazoncorretto:21-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# 프로젝트 루트에서 빌드&JAR(패키지) 생성
./gradlew bootJar

# JAR(패키지) 생성 위치 확인
step-5-spring-boot-single/
├── build/
│   └── libs/
│       └── step-5-spring-boot-single-0.0.1-SNAPSHOT.jar  ← 생성된 JAR
├── docker/
│   └── Dockerfile       ← Docker 빌드 설정
├── docker-compose.yml
└── src/
```

```bash
# 도커 이미지 빌드
# 프로젝트 루트에서 실행
docker build -t step5-app -f docker/Dockerfile .
             │            │                    │
             │            │                    └─ 빌드 컨텍스트 (COPY 기준)
             │            └─ Dockerfile 위치
             └─ 이미지 이름

# 파일 구조
step-5-spring-boot-single/     ← 빌드 컨텍스트 (.)
├── docker/
│   └── Dockerfile             ← -f 옵션으로 지정
├── build/
│   └── libs/
│       └── app.jar            ← COPY 대상 (빌드 컨텍스트 기준)
└── src/

# 빌드 컨텍스트 
Dockerfile의 COPY, ADD 명령어가 참조하는 기준 경로
.은 현재 디렉토리를 빌드 컨텍스트로 지정
빌드 컨텍스트 내의 파일만 Docker 이미지에 복사 가능
 
# 도커 빌드 옵션
-t, --tag	: 이미지 이름:태그 지정
-f, --file	: Dockerfile 경로 지정
--no-cache	: 캐시 없이 빌드
--build-arg	: 빌드 시 변수 전달	(ex.--build-arg JAR_FILE=app.jar)
```

```bash
# 생성된 이미지(목록) 확인
docker images

[출력]
REPOSITORY    TAG       IMAGE ID       CREATED          SIZE
step5-app     latest    a1b2c3d4e5f6   10 seconds ago   300MB
```


#### 2-1. Docker 이미지 실행 (docker 명령어)

```bash
# 이미지 실행
# docker run [옵션] <이미지명>
docker run -d -p 8080:8080 --name my-app -e SPRING_PROFILES_ACTIVE=prod step5-app
           │  │            │             │                                │
           │  │            │             │                                └─ 이미지
           │  │            │             └─ 환경 변수
           │  │            └─ 컨테이너 이름
           │  └─ 포트 매핑 (호스트:컨테이너)
           └─ 백그라운드 실행 ( 제거하면 포그라운드로 실행 )


# 주요 옵션
-d, --detach  :	백그라운드 실행
-p, --publish :	포트 매핑
--name	      : 컨테이너 이름 지정
-e, --env	  : 환경 변수 설정 	
-v, --volume  : 볼륨 마운트 (컨테이너에서 생성한 데이터를 호스트에 저장)
--rm	      : 종료 시 컨테이너 자동 삭제
-it	          : 대화형 터미널 연결	
```

#### 2-1. Docker 이미지 실행 (docker compose 명령어)
이런 방식으로는 잘 안씀.  
실무에서는 이미지를 컨테이너 저장소(Docker Hub, ECR 등)에 올려두고 pull해서 사용하는 경우가 많음.  
프로젝트 내 docker-compose.yml에는 앱 실행에 필요한 외부 서비스(DB, Redis 등)를 함께 정의하는 경우가 많음.
```bash
# docker-compose.yml 
services:
  spring-boot-single:
    build:
      context: .                    # 빌드 컨텍스트 (COPY 기준 경로)
      dockerfile: docker/Dockerfile # Dockerfile 위치**
    ports:
      - "8080:8080"             # 포트 매핑
    environment:
      - SPRING_PROFILES_ACTIVE=prod


# docker compose [명령어] [옵션]
docker compose up -d --build
       │       │  │  │
       │       │  │  └─ 이미지 빌드 후 실행
       │       │  └─ 백그라운드 실행
       │       └─ 컨테이너 생성 및 시작
       └─ compose 명령어
```

#### 3. Docker 실행 상태 확인
```bash
# 실행 상태 확인
# 실행 중인 컨테이너만
docker ps

# 모든 컨테이너 (중지된 것 포함)
docker ps -a

[출력]
CONTAINER ID   IMAGE       COMMAND                  STATUS          PORTS                    NAMES
f1e2d3c4b5a6   step5-app   "java -jar app.jar"      Up 30 seconds   0.0.0.0:8080->8080/tcp   my-app
```


#### 3. Docker 실행 환경 접속

```bash
# 컨내이너 내부 접속
# docker exec -it <컨테이너명> sh
docker exec -it my-app sh
       │    │   │      │
       │    │   │      └─ 실행할 쉘 (또는 bash)
       │    │   └─ 컨테이너 이름 (CONTAINER ID로도 실행가능)
       │    └─ 대화형 터미널 연결 (-i: 입력 유지, -t: 터미널 할당)
       └─ 실행 중인 컨테이너에 명령어 실행

# 주요 옵션
-i, --interactive : 표준 입력 유지	
-t, --tty	      : 터미널 할당	
-it	              : 대화형 터미널 (위 두 옵션 조합)
-u, --user	      : 사용자 지정	
-w, --workdir	  : 작업 디렉토리 지정(터미널 시작 지점)

# 컨테이너 쉘 종료
exit
```

```bash
# 내부 파일 구조 확인
docker exec -it my-app sh

# 초기 경로는 기본 작업 경로(WORKDIR)로 세팅 되어있음.  

# 파일 목록 확인
ls -la

[출력]
total 45678
drwxr-xr-x 1 root root     4096 Jan 15 10:00 .
drwxr-xr-x 1 root root     4096 Jan 15 10:00 ..
-rw-r--r-- 1 root root 45678901 Jan 15 10:00 app.jar


# `cd ../` 명령어로 상위 폴더로 가서 `ls -al`을  아래 컨테이너 구조가 보임
# 컨테이너 내부 구조
/
├── app/                 ← WORKDIR
│   └── app.jar          ← COPY로 복사된 JAR
├── usr/
│   └── lib/
│       └── jvm/         ← JRE (FROM 이미지에 포함)
└── ...

# 컨테이너 쉘 종료
exit
```

```bash
# 로그 확인
# docker logs <컨테이너명>
docker logs -f my-app
       │    │   │
       │    │   └─ 컨테이너 이름
       │    └─ 실시간 로그 출력
       └─ 로그 출력 명령어

# 주요 옵션
-f, --follow     : 실시간 로그 출력
--tail	         : 마지막 N줄만 출력
-t, --timestamps : 타임스탬프 포함
--since	         : 특정 시간 이후 로그

# 컨테이너 쉘 종료
exit
```

#### 4. Docker 컨테이너 종료
```bash
# 컨테이너 종료
docker stop <컨테이너명>
```

#### 4. Docker 컨테이너 제거
```bash
# 컨테이너 제거
docker rm <컨테이너명>
```


## Docker 실행 환경 ( 좀 더 깊게 보기 ) 
> 💡 이해 안 되면 일단 무시해도 괜찮음. 나중에 필요할 때 다시 보기.


### 질문
아래 내용에서 Docker Engine은 어떻게 동작할까?

```
# docker-compose.yml

services:
  A:
    image: Service_A
    ports:
      - "9090:8080"       # 외부 9090 → 내부 8080

  B:
    image: Service_B
    ports:
      - "9091:8080"       # 외부 9091 → 내부 8080 (A와 내부 포트 동일해도 OK)

  mysql:
    image: mysql:8.0
    ports:
      - "13306:3306"      # 외부 13306 → 내부 3306
      

┌──────────────────────────────────────────────────────────────────┐
│  호스트 (내 PC / 서버)                                              │
│                                                                  │
│   localhost:9090    localhost:9091    localhost:13306            │
│        │                 │                 │                     │
│        ▼                 ▼                 ▼                     │
│   ┌─────────┐       ┌─────────┐       ┌─────────┐                │
│   │ :9090   │       │ :9091   │       │ :13306  │  ◀─ 외부 포트    │
│   └────┬────┘       └────┬────┘       └────┬────┘     (중복불가)   │
│        │                 │                 │                     │
│   ─────┼─────────────────┼─────────────────┼──── 포트 매핑 ────    │
│        │                 │                 │                     │
│   ┌────┼─────────────────┼─────────────────┼─────────────────┐   │
│   │    │                 │                 │                 │   │
│   │    │        Docker Engine (Docker 환경) │                 │   │
│   │    │                 │                 │                 │   │
│   │    ▼                 ▼                 ▼                 │   │
│   │ ┌──────┐         ┌──────┐         ┌──────┐               │   │
│   │ │:8080 │         │:8080 │         │:3306 │  ◀─ 내부 포트   │   │
│   │ │  A   │         │  B   │         │MySQL │               │   │
│   │ └──────┘         └──────┘         └──────┘               │   │
│   │                                                          │   │
│   │              컨테이너들 (독립된 격리 환경)                      │   │
│   └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

### Docker는 어떤 환경에서 실행되는가?
Docker는 **Linux 커널 기능**을 사용하는 프로그램
컨테이너 격리를 위해 Linux의 namespaces, cgroups 기능 필요

| Linux 커널 기능 | 역할 |
|-----------------|------|
| namespaces | 프로세스, 네트워크, 파일시스템 격리 |
| cgroups | CPU, 메모리 자원 제한 |

### OS별 Docker 동작 방식
| OS | 동작 방식 | 설명 |
|-----|-----------|------|
| Linux | 네이티브 | 직접 커널 사용 (VM 없음, 빠름) |
| macOS | 경량 Linux VM | Docker Desktop이 HyperKit으로 VM 실행 |
| Windows | 경량 Linux VM | Docker Desktop이 WSL2로 VM 실행 |


### Linux (네이티브 실행)
```
VM 없이 직접 실행 (빠름)

┌─────────────────────────┐
│       Linux OS          │
├─────────────────────────┤
│     Docker Engine       │  ← 직접 커널 사용
├───────┬───────┬─────────┤
│ App A │ App B │  MySQL  │
└───────┴───────┴─────────┘
```


### Mac / Windows (VM 위에서 실행)
```
VM을 통해서 실행 (느려짐)
┌─────────────────────────────────────────────────────────────────┐
│  macOS / Windows                                                │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │              Docker Desktop                             │   │
│   │   ┌─────────────────────────────────────────────────┐   │   │
│   │   │          경량 Linux VM                           │   │   │
│   │   │         (HyperKit / WSL2)                       │   │   │
│   │   │   ┌─────────────────────────────────────────┐   │   │   │
│   │   │   │           Docker Engine                 │   │   │   │
│   │   │   │                                         │   │   │   │
│   │   │   │   ┌───────┐ ┌───────┐ ┌───────┐         │   │   │   │
│   │   │   │   │ App A │ │ App B │ │ MySQL │         │   │   │   │
│   │   │   │   └───────┘ └───────┘ └───────┘         │   │   │   │
│   │   │   │          컨테이너들                        │   │   │   │
│   │   │   └─────────────────────────────────────────┘   │   │   │
│   │   └─────────────────────────────────────────────────┘   │   │
│   └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 왜 Mac/Windows에서도 잘 동작하나?

Docker Desktop이 내부적으로 경량 Linux VM을 자동으로 실행하고 관리함.  
사용자는 VM 존재를 신경 쓸 필요 없이 동일한 docker 명령어를 사용할 수 있음.


### 정리
 Docker = Linux 커널 기능 필요
- Linux   → 직접 실행 (네이티브)
- Mac     → Docker Desktop → 경량 Linux VM → Docker Engine
- Windows → Docker Desktop → WSL2 VM → Docker Engine

> 사용자 입장에서는 OS 상관없이 동일하게 사용 가능

## 다음 단계

step-6에서 `app-service`, `admin-service`로 분리.

## 참고
- `build-logs/`에 빌드/실행 로그 보관.
