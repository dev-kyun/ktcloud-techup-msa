# 수동 빌드 예제

이 디렉토리는 Java 애플리케이션을 수동으로 빌드하고 실행하는 방법을 보여줌.

## 이 예제의 위치

`step-1-pure-manual-build/` 다음 단계 예제.
클래스 직접 실행에서 JAR + MANIFEST.MF 방식으로 확장.

## 차이점과 목표

- **step-1-pure-manual-build**: 가장 단순한 컴파일/직접 실행
- **step-2-manual-build**: 패키지 구조 + JAR + MF 기반 실행

목표
- Main 지정 위치(실행 시점 vs 패키징 시점) 차이 이해
- JAR로 실행/공유가 단순해지는 이유 이해

## JAR 간단 설명
Java ARchive (자바 전용 압축 파일. 그냥 압축파일. 해제해서 내부 볼 수 있음. )  
JAR은 여러 클래스 파일을 하나로 묶은 실행 패키지.   
장점: 실행 명령 단순화(이런 저런 옵션, 경로 설정 생략하고 명령 단순함), 공유/배포 용이(하나로 압축했으니까), 용량   

## 빌드 과정

```
.java → [컴파일] → .class → [패키징] → .jar
```

## 파일 구조

- `src/com/kyun/example/app/Main.java` - 메인 클래스
- `src/com/kyun/example/service/UserService.java` - 서비스 클래스
- `src/com/kyun/example/repository/UserRepository.java` - 리포지토리 클래스
- `build-manual.sh` - 수동 빌드 자동화 스크립트
- `run-manual.sh` - 실행 스크립트

## 실행 방법

### 1. 빌드
```bash
# 수동 빌드 스크립트 실행
./build-manual.sh
```

이 스크립트는 다음 단계를 자동으로 수행:
1. `javac`로 Java 소스코드 컴파일
2. `MANIFEST.MF` 파일 생성 (Main-Class 지정)
3. `jar cvfm`으로 JAR 파일 패키징
4. `java -jar`로 실행 테스트

### 2. 실행
```bash
# 실행 스크립트 실행
./run-manual.sh
```

## MF 방식 필요성

패키지 구조가 생기면 실행 시 클래스패스와 전체 클래스명 직접 지정 필요.  
JAR + MANIFEST.MF를 쓰면 `java -jar`로 단순 실행 가능.  
Main-Class 지정, 기타 정보 지정 가능  
( 조금 더 우아하게 처리 가능 )  

직접 실행 예시:
```bash
# -cp: 클래스패스 지정, Main 직접 실행
java -cp build-output/classes com.kyun.example.app.Main # 또는 build-output/classes 경로로 이동한 뒤 `java com.kyun.example.app.Main`
```

MF 없이 JAR 실행 예시(실패 예시):
```bash
# c: create, v: verbose, f: output file
# -C: build-output/classes로 이동 후 모든 파일 포함
# Main-Class 없이 JAR 생성 → java -jar 실행 실패
jar cvf myapp.jar -C build-output/classes .
# -jar: JAR의 Main-Class로 실행 시도
java -jar myapp.jar
```
`Main-Class`가 없어 실행 실패. ( 시작점을 알수 없음 )

## MF 메타정보 활용 예시

코드에서 `.MF파일에 작성한 Implementation-Version`값 확인 가능.
```java
String version = Main.class.getPackage().getImplementationVersion();
System.out.println("Implementation-Version: " + (version == null ? "(none)" : version));
```


### 3. 수동 빌드 & 실행 (Gradle 미사용)

#### 컴파일
```bash
# -r: 하위 포함 삭제, -f: 에러 없이 강제 삭제
rm -rf build-output
# -p: 상위 경로까지 함께 생성
mkdir -p build-output/classes
# -d: 컴파일 결과(.class) 출력 경로 지정
javac -d build-output/classes \
  src/com/kyun/example/app/Main.java \
  src/com/kyun/example/service/UserService.java \
  src/com/kyun/example/repository/UserRepository.java
```



#### MANIFEST.MF 생성
```bash
# >: 파일 생성/덮어쓰기, EOF: 여기까지 내용을 기록
cat > build-output/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: com.kyun.example.app.Main
Implementation-Title: step-2-manual-build
Implementation-Version: 0.0.1-SNAPSHOT
Build-Jdk-Spec: 21

EOF
```




#### JAR 패키징
```bash
# c: create(새로운 jar파일 생성), v: verbose(처리로그 상세히 출력), f: output file(JAR 파일명 지정), m: manifest 지정(매니페스트 파일 지정)
# -C: 디렉토리 변경 후 작업 수행 ( build-output/classes로 이동 후 모든 파일 포함한다. )
#jar cvfm [JAR파일] [매니페스트파일] [포함할내용]  ( fm -> 순서 중요 )
#         ↓         ↓
#         f옵션     m옵션  ( 실행 가능한 JAR 생성, 로그 상세히, MANIFEST.MF 포함해서, output 디렉토리 지정 )
# ( 그냥 규칙이라서 문서나 AI 질문하면서 필요한 명령어, 옵션 잘 골라서 실행하면 됨. ) 
jar cvfm build-output/myapp.jar build-output/MANIFEST.MF -C build-output/classes .
```

#### 실행
```bash
# -jar: JAR의 Main-Class로 실행
java -jar build-output/myapp.jar
```

## 예상 출력
```
홍길동
Implementation-Version: 0.0.1-SNAPSHOT
```

## 빌드 단계별 설명

| 단계 | 명령어 | 결과물 | 설명 |
|------|--------|--------|------|
| 컴파일 | `javac *.java` | `.class` 파일들 | 소스코드를 바이트코드로 변환 |
| 매니페스트 생성 | `cat > MANIFEST.MF` | `MANIFEST.MF` | JAR 실행 시 시작점 정보 |
| 패키징 | `jar cvf myapp.jar` | `myapp.jar` | 실행 가능한 JAR 파일 생성 |
| 실행 | `java -jar myapp.jar` | 콘솔 출력 | 애플리케이션 실행 |



## 참고
#### build-output

`build-output/`에 학습용 빌드 결과물을 보관.
`classes/`, `MANIFEST.MF`, `myapp.jar`, `output.txt` 포함.
`output.txt`는 실행 결과를 저장한 로그 파일.

#### build-logs

`build-logs/`에 빌드 로그/요약 파일 보관.


#### 만약 라이브러리를 사용한다면?
```
더 복잡한 작업이 필요... 
Gradle에서 쉽고 편하게 다룰 수 있음. ( 다음 스텝으로.. )
방법 1: 클래스패스에 모든 JAR 나열
java -cp "build-output/classes:lib/gson-2.10.jar:lib/mysql-connector-8.0.jar" com.kyun.example.app.Main

방법 2: MANIFEST.MF에 Class-Path 지정
(MANIFEST.MF 내부)
Class-Path: lib/gson-2.10.jar lib/mysql-connector-8.0.jar
```
