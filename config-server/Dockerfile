# syntax=docker/dockerfile:1

# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /workspace/app

# Copy parent POM
COPY pom.xml /workspace/pom.xml

# Copy service files
COPY config-server/pom.xml ./pom.xml
COPY config-server/src ./src

# Build the service
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f /workspace/pom.xml install -DskipTests -N && \
    mvn clean package -DskipTests

# Extract layers
RUN java -Djarmode=layertools -jar target/*.jar extract --destination /workspace/extracted

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

RUN apk upgrade --no-cache && \
    apk add --no-cache curl dumb-init && \
    rm -rf /var/cache/apk/*

RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser

WORKDIR /app

COPY --from=builder --chown=appuser:appuser /workspace/extracted/dependencies/ ./
COPY --from=builder --chown=appuser:appuser /workspace/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appuser /workspace/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appuser /workspace/extracted/application/ ./

USER appuser

ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+UseG1GC"

EXPOSE 8888

HEALTHCHECK --interval=30s --timeout=10s --start-period=45s --retries=3 \
    CMD curl -f http://localhost:8888/actuator/health || exit 1

ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
