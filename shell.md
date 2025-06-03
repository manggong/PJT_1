💡 전제 조건
Spring Boot는 jar로 패키징됨.

배포 대상 서버는 Red Hat 기반 Linux.

Java 17 이상이 설치되어 있음.

배포 디렉토리는 /opt/myapp.

Spring Boot는 nohup으로 실행되며 백그라운드에서 동작.

build.gradle 또는 pom.xml에서 version 정보를 가져올 수 있다고 가정.

📁 디렉토리 구조 (권장)
/opt/myapp/
├── current/ # 현재 실행 중인 버전
├── backups/ # 이전 버전 백업
├── logs/ # 로그 저장
└── deploy.sh # 배포 스크립트
✅ 1. 최초 배포 스크립트 deploy.sh
#!/bin/bash

APP_NAME="myapp"
JAR_NAME="myapp.jar"
DEPLOY_DIR="/opt/myapp"
TARGET_JAR="./build/libs/$JAR_NAME" # build 후 jar 경로

# 1. 빌드된 jar 복사

mkdir -p "$DEPLOY_DIR/current"
cp "$TARGET_JAR" "$DEPLOY_DIR/current/$JAR_NAME"

# 2. 실행

cd "$DEPLOY_DIR/current" || exit
nohup java -jar "$JAR_NAME" > "$DEPLOY_DIR/logs/$APP_NAME.log" 2>&1 &
echo "Spring Boot 앱이 배포되었습니다."
🔁 2. 재배포 스크립트 redeploy.sh (버전 감지 + 백업 포함)
#!/bin/bash

APP_NAME="myapp"
DEPLOY_DIR="/opt/myapp"
JAR_DIR="./build/libs"
JAR_FILE=$(find "$JAR_DIR" -name "$APP_NAME*.jar" | head -n 1)
VERSION=$(echo "$JAR_FILE" | sed -n 's/.*'"$APP_NAME"'-\(.\*\)\.jar/\1/p')
CURRENT_JAR="$DEPLOY_DIR/current/$APP_NAME.jar"

# 경로 준비

mkdir -p "$DEPLOY_DIR/backups"
mkdir -p "$DEPLOY_DIR/logs"

# 현재 실행 중이면 종료

CURRENT_PID=$(pgrep -f "$APP_NAME.jar")
if [ -n "$CURRENT_PID" ]; then
echo "기존 프로세스 종료: $CURRENT_PID"
    kill "$CURRENT_PID"
sleep 2
fi

# 현재 버전 백업

if [ -f "$CURRENT_JAR" ]; then
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
    cp "$CURRENT*JAR" "$DEPLOY_DIR/backups/${APP_NAME}*$TIMESTAMP.jar"
    echo "기존 버전 백업 완료: ${APP_NAME}_$TIMESTAMP.jar"
fi

# 새로운 jar 복사 및 실행

cp "$JAR_FILE" "$CURRENT_JAR"
cd "$DEPLOY_DIR/current" || exit
nohup java -jar "$APP_NAME.jar" > "$DEPLOY_DIR/logs/$APP_NAME.log" 2>&1 &
echo "버전 [$VERSION] 으로 재배포 완료"
🧪 사용 예시
최초 배포

./deploy.sh
빌드 후 재배포

./gradlew build
./redeploy.sh
📌 참고사항
pgrep -f "$APP_NAME.jar" 로 실행 중인 프로세스를 찾고 종료합니다.

nohup 실행 시 로그는 /opt/myapp/logs/ 아래로 저장됩니다.

backups/ 디렉토리에는 기존 jar가 타임스탬프와 함께 백업됩니다.

build/libs/myapp-버전.jar 형태여야 버전 추출이 정상 작동합니다.

필요하면 systemd 등록 방식도 추가로 설명드릴 수 있습니다.

---

✅ 1. 서비스 등록 (systemd 사용)
Spring Boot 앱을 Linux 서비스로 등록하면 재부팅 후 자동 실행 및 status/restart 관리가 편리합니다.

📄 /etc/systemd/system/myapp.service
[Unit]
Description=Spring Boot Application - MyApp
After=network.target

[Service]
User=root
ExecStart=/usr/bin/java -jar /opt/myapp/current/myapp.jar
WorkingDirectory=/opt/myapp/current
StandardOutput=append:/opt/myapp/logs/myapp.log
StandardError=append:/opt/myapp/logs/myapp.err.log
Restart=on-failure
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
▶️ 명령어

# systemd 등록

sudo systemctl daemon-reexec
sudo systemctl daemon-reload

# 시작 및 등록

sudo systemctl start myapp
sudo systemctl enable myapp

# 상태 확인

sudo systemctl status myapp
📈 2. 헬스 체크 및 모니터링 (Actuator 활용)
✅ Spring Boot Actuator 설정
build.gradle

implementation 'org.springframework.boot:spring-boot-starter-actuator'
application.yml

management:
endpoints:
web:
exposure:
include: health, info, metrics, prometheus
endpoint:
health:
show-details: always
📡 활용 가능한 URL
엔드포인트 설명
/actuator/health 앱 헬스 체크 (상태 확인)
/actuator/info 앱 메타 정보
/actuator/metrics 메트릭 (CPU, 메모리, 요청 수 등)
/actuator/prometheus Prometheus용 메트릭 (모니터링 연동용)
🔁 시스템 모니터링 연동 예시
Prometheus + Grafana: /actuator/prometheus 사용

ELK (Elasticsearch + Logstash + Kibana)와 연동하려면 로그를 파일로 남기고 Logstash 또는 Filebeat로 수집

📋 3. 로깅 설정 (logback + 파일 분리)
✅ logback-spring.xml
<configuration>

    <property name="LOG_PATH" value="/opt/myapp/logs" />

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/myapp.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/myapp.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>[%d{yyyy-MM-dd HH:mm:ss}] [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>

</configuration>
참고: Spring Boot 기본 로그는 logback-spring.xml 또는 application.yml에서 설정 가능.

📌 정리된 체크리스트
항목 설정 방식 설명
서비스 등록 systemd 서비스 자동시작 및 상태 관리
헬스 체크 Spring Boot Actuator /actuator/health 활용
모니터링 연동 Prometheus, Grafana /actuator/prometheus
로그 관리 Logback + Rolling 일자별 로그 분리 및 파일 보존
수집 시스템 연동 ELK 또는 Filebeat 로그 파일 수집 및 시각화 가능
필요하시면 Grafana 대시보드 예시, Filebeat 설정, 또는 로그 슬랙 알림 연동도 추가로 설명해 드릴 수 있습니다.

---

✅ 1. Actuator 자체는 JSON API 기반
/actuator 이하 경로들은 RESTful JSON 형태로 제공됩니다:

/actuator/health → 앱 상태 확인

/actuator/metrics/jvm.memory.used → JVM 메모리 사용량 등

/actuator/httptrace (Spring Security 사용 시 활성화 가능) → 최근 HTTP 요청

❌ 하지만 이 API들은 웹 UI가 아니라 단순 JSON 응답입니다.

✅ 2. 시각화 UI를 제공하려면?
🔹 방법 1: Spring Boot Admin (추천)
Spring Boot Actuator 기반의 웹 UI 대시보드를 제공해주는 외부 프로젝트입니다.

여러 애플리케이션 상태를 한눈에 모니터링

Health, Log Level 변경, Metrics, 환경 변수 확인

기본 로그인 지원

Slack 등 알림 연동 가능

🛠 설정 방법
의존성 추가 (관리 서버 기준)

implementation 'de.codecentric:spring-boot-admin-starter-server'
의존성 추가 (클라이언트 앱 기준)

implementation 'de.codecentric:spring-boot-admin-starter-client'
관리 서버 설정 예시

spring:
boot:
admin:
ui:
title: MyApp Monitoring
클라이언트 앱 설정 예시

spring:
boot:
admin:
client:
url: http://localhost:8080 # 관리 서버 주소
management:
endpoints:
web:
exposure:
include: "\*"
👉 결과: 웹 브라우저에서 http://localhost:8080 접속 시 UI 제공

🔹 방법 2: Prometheus + Grafana
/actuator/prometheus 노출 → Prometheus가 메트릭 수집

Grafana가 이를 시각화 UI로 제공

CPU, 메모리, GC, 요청 처리량, TPS 등의 시계열 분석 가능

✅ 이 조합은 운영환경에서 가장 많이 쓰이는 모니터링 스택

🔹 방법 3: Spring Cloud + Sleuth + Zipkin
Actuator 외에 분산 추적을 위한 Sleuth + Zipkin 도입

Zipkin은 기본 UI를 제공

요청의 병목, 응답 시간 등을 추적 가능

🔚 요약
방법 UI 제공 특징
❌ Actuator 단독 없음 JSON API 응답만
✅ Spring Boot Admin 있음 Actuator 기반 전용 대시보드
✅ Grafana + Prometheus 있음 시계열 데이터 시각화
✅ Zipkin 있음 분산 추적 시각화
🔄 직접 구현 커스텀 Actuator API를 이용해 React/Vue로 구현 가능
필요하신 방식(Spring Boot Admin 또는 Grafana 연동 등)이 있으면, 그에 맞는 설치 및 설정도 상세히 도와드릴 수 있습니다.

---

✅ 1. Spring Boot Actuator 자체는 로그 UI를 제공하지 않습니다
Actuator는 애플리케이션 상태, 메트릭, 헬스 체크는 제공하지만:

❌ 로그 자체를 수집하거나 보여주지는 않습니다.

/actuator/logfile endpoint가 있긴 하지만, 보안 상 기본 비활성화 상태이며 전체 로그 스트림을 제공하는 수준 (불편하고 위험).

✅ 2. Spring Boot Admin – 제한적 로그 보기 가능
Spring Boot Admin은 다음 기능들을 제공합니다:

기능 가능 여부
로그 레벨 변경 ✅ 가능 (/actuator/loggers API 활용)
로그 보기 (실시간) ❌ 직접 로그는 보이지 않음
로그 보기 (간접) ❌ 로그파일 직접 접근은 안됨
즉, Spring Boot Admin만으로는 로그 보기 기능이 제한적입니다. 로그 수준(Level) 변경은 가능하지만 로그 내용을 UI로 보진 못합니다.

✅ 3. 로그를 시각적으로 보고 싶다면?
🔹 선택 1: ELK Stack (Elasticsearch + Logstash + Kibana)
가장 강력한 로그 수집 및 시각화 스택입니다.

logback이나 log4j로 남긴 로그파일을 Logstash 또는 Filebeat가 수집

Elasticsearch에 저장

Kibana UI에서 로그를 검색, 필터링, 시각화 가능

장점
실시간 검색, 필터

태그별 로그 분석

대시보드 작성 가능

🔹 선택 2: Grafana Loki
Grafana에서 제공하는 로그 수집 및 시각화 시스템.

Prometheus와 유사한 구조

로그를 Loki에 보내면 Grafana UI에서 검색/필터 가능

파일을 직접 tail하거나, promtail로 수집

장점
Grafana UI에서 메트릭 + 로그 통합 보기 가능

설정이 상대적으로 간단함

🔹 선택 3: 커스텀 로그 뷰어
Actuator의 /actuator/logfile 또는 로그파일을 직접 읽는 REST API를 만들어서 React/Vue 등으로 UI 구현도 가능하지만:

보안 및 성능 문제가 큼

운영 환경에서는 거의 사용하지 않음

🔍 결론: 어떤 방식이 로그 UI 제공 가능한가?
도구 로그 UI 제공 실시간 확인 운영 환경 적합
Actuator ❌ 없음 일부 로그 파일 출력 가능 (/logfile) ❌
Spring Boot Admin ❌ 제한적 로그 수준 변경만 가능 🔶
ELK (Kibana) ✅ 로그 UI 제공 ✅ 가능 ✅ 가장 강력
Grafana + Loki ✅ 로그 UI 제공 ✅ 가능 ✅
직접 구현 (React 등) 🔶 가능 제한적 ❌ 비추천
🎯 추천 조합
운영 환경: Actuator + Spring Boot Admin + ELK 또는 Grafana Loki

간편한 개발 환경: Spring Boot Admin만 사용하고 로그는 tail -f로 확인

중소 규모 운영환경: Grafana + Loki → 설정은 간단하지만 강력함
