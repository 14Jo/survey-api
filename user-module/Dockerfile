FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

COPY shared-kernel/build.gradle shared-kernel/
COPY shared-kernel/src/ shared-kernel/src/

COPY user-module/build.gradle user-module/
COPY user-module/src/ user-module/src/

RUN ./gradlew :user-module:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine AS runtime

RUN apk add --no-cache curl

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /app/user-module/build/libs/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
