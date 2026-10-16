# finNS 배포 이미지 - 프론트 빌드 → WAR 빌드 → Tomcat 실행
# GitHub Actions(.github/workflows/deploy.yml)가 빌드해 ghcr.io 에 올리고, 서버는 받아서 실행만 한다.
# 빌드 단계는 빌드 머신의 CPU 종류로 한 번만 돌고(결과물이 CPU와 무관), 마지막 Tomcat 단계만 서버 CPU(amd64/arm64)별로 만든다.

# 1) Vue 빌드
FROM --platform=$BUILDPLATFORM node:22-slim AS frontend
WORKDIR /app/FInNS-FrontEnd
COPY FInNS-FrontEnd/package.json FInNS-FrontEnd/package-lock.json ./
RUN npm ci
COPY FInNS-FrontEnd/ ./
RUN npm run build

# 2) WAR 빌드 (프론트 빌드 결과물을 WAR 안에 넣는다 - FInNS-BackEnd/build.gradle)
FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jdk AS backend
WORKDIR /app/FInNS-BackEnd
COPY FInNS-BackEnd/gradlew FInNS-BackEnd/build.gradle FInNS-BackEnd/settings.gradle ./
COPY FInNS-BackEnd/gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon --version
COPY FInNS-BackEnd/src ./src
COPY --from=frontend /app/FInNS-FrontEnd/dist /app/FInNS-FrontEnd/dist
RUN ./gradlew --no-daemon test war

# 3) 실행
FROM tomcat:9.0-jre17-temurin
RUN rm -rf /usr/local/tomcat/webapps/* \
    && mkdir -p /var/lib/finns/upload
COPY --from=backend /app/FInNS-BackEnd/build/libs/finns.war /usr/local/tomcat/webapps/ROOT.war
ENV UPLOAD_DIR=/var/lib/finns/upload
EXPOSE 8080
