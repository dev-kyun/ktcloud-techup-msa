#!/bin/bash

echo "=== 수동 빌드 애플리케이션 실행 ==="

if [ ! -f "myapp.jar" ]; then
    echo "myapp.jar 파일이 없습니다. 먼저 build-manual.sh를 실행하세요."
    exit 1
fi

echo "애플리케이션 실행..."
java -jar myapp.jar

echo "=== 실행 완료 ==="