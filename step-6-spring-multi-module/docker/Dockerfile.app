# 이미지는 기반 OS/런타임 + 앱 + 실행 명령으로 구성됨
# 필수 요소: FROM(기반), COPY/ADD(앱), ENTRYPOINT/CMD(실행)
# 자주 사용: WORKDIR, EXPOSE, ENV, RUN
# 예: WORKDIR /app, EXPOSE 8081, ENV SPRING_PROFILES_ACTIVE=prod
# JDK 도구 예: javac, jar, javadoc, jlink
# 회사 정책/운영 환경에 따라 베이스 OS를 선택할 수 있음
# FROM: 컨테이너의 기반 OS와 런타임을 정의
# Amazon Corretto: AWS 공식 OpenJDK 배포판
FROM amazoncorretto:21
# 작업 디렉토리 설정
WORKDIR /app
# 빌드된 JAR 복사
COPY app-service/build/libs/app-service.jar app.jar
# 컨테이너가 사용하는 포트 문서화 ( 실제로 호스트로 노출되거나 연결되는 옵션은 아님. 문서상 안내 역할. 이 포트를 쓸 사용할 예정이니 참고하라는 정도 )
EXPOSE 8081
# 컨테이너 시작 시 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
