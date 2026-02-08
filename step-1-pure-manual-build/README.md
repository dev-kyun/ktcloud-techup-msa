# 순수 Java 수동 빌드 예제

이 폴더는 **순수 Java 파일만 있을 때** 수동으로 빌드하고 실행하는 방법을 단계적으로 보여줌.
가장 단순한 방식만 다룸.

다음 단계는 `step-2-manual-build/`에서 JAR + MANIFEST.MF 방식으로 확장.

**설명**: `.java`를 컴파일한 뒤, 바로 `java Main`으로 실행.

**명령어**
```bash
# ~/step-1-pure-manual-build/ 경로로 이동
# 이전 빌드 산출물 정리
# -r: 하위 포함 삭제, -f: 에러 없이 강제 삭제
rm -rf build-output
# 컴파일 결과를 저장할 디렉토리 생성
# -p: 상위 경로까지 함께 생성
mkdir -p build-output/classes
# .java를 .class로 컴파일해서 build-output/classes에 저장
# -d: 컴파일 결과(.class) 출력 경로 지정
javac -d build-output/classes Main.java UserService.java UserRepository.java
# 클래스패스를 지정해 Main 실행
# -cp: 클래스패스 지정
java -cp build-output/classes Main # 또는 build-output/classes 경로로 이동한 뒤 `java Main` 명령어 실행
```

실행 결과를 파일로 저장하려면:
```bash
# -cp: 클래스패스 지정, >: 출력 리다이렉트
java -cp build-output/classes Main > build-output/output.txt
```

##  참고
```bash
# -r: 하위 포함 삭제, -f: 에러 없이 강제 삭제
rm -rf build-output/classes
```

#### build-output

`build-output/`에 학습용 컴파일 결과물 보관.
`classes/`, `output.txt` 포함.

#### build-logs

이 예제는 별도 빌드 로그 파일 없음.
