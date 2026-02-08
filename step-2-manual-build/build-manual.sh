#!/bin/bash

echo "=== 수동 빌드 시작 ==="

# 1. 컴파일
echo "1. Java 소스코드 컴파일..."
echo "- 이전 빌드 산출물 정리"
rm -rf build-output
mkdir -p build-output/classes
javac -d build-output/classes \
    src/com/kyun/example/app/Main.java \
    src/com/kyun/example/service/UserService.java \
    src/com/kyun/example/repository/UserRepository.java

if [ $? -ne 0 ]; then
    echo "컴파일 실패!"
    exit 1
fi

echo "컴파일 완료!"
find build-output/classes -type f -name "*.class"

# 2. MANIFEST.MF 생성
echo "2. MANIFEST.MF 파일 생성..."
cat > build-output/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: com.kyun.example.app.Main
Implementation-Title: step-2-manual-build
Implementation-Version: 0.0.1-SNAPSHOT
Build-Jdk-Spec: 21

EOF

# 3. JAR 패키징
echo "3. JAR 파일 패키징..."
jar cvfm build-output/myapp.jar build-output/MANIFEST.MF -C build-output/classes .

if [ $? -ne 0 ]; then
    echo "JAR 패키징 실패!"
    exit 1
fi

echo "JAR 패키징 완료!"
ls -la build-output/myapp.jar

# 4. 실행 테스트
echo "4. 애플리케이션 실행 테스트..."
java -jar build-output/myapp.jar > build-output/output.txt 2>&1

if [ $? -eq 0 ]; then
    echo "실행 성공!"
    echo "출력 결과:"
    cat build-output/output.txt
else
    echo "실행 실패!"
    cat build-output/output.txt
    exit 1
fi

echo "=== 수동 빌드 완료 ==="
