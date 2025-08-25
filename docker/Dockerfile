# Multi-stage build for optimized production image

# Stage 1: Build stage
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY ../gradle gradle/
COPY ../gradlew .
COPY ../build.gradle .
COPY ../settings.gradle .

# Copy all module build files
COPY ../shared-kernel/build.gradle shared-kernel/
COPY ../user-module/build.gradle user-module/
COPY ../project-module/build.gradle project-module/
COPY ../survey-module/build.gradle survey-module/
COPY ../participation-module/build.gradle participation-module/
COPY ../statistic-module/build.gradle statistic-module/
COPY ../share-module/build.gradle share-module/
COPY ../web-app/build.gradle web-app/

# Download dependencies (for better caching)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY .. .

# Build application
RUN ./gradlew :web-app:bootJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /app/web-app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization for container environment
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]