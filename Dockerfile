# =========================
# Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy toàn bộ project
COPY . .

# Kiểm tra file pom.xml
RUN ls -la

# Build project
RUN mvn clean package -DskipTests

# =========================
# Runtime stage
# =========================
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.24

WORKDIR /deployments

COPY --from=builder /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder /app/target/quarkus-app/*.jar /deployments/
COPY --from=builder /app/target/quarkus-app/app/ /deployments/app/
COPY --from=builder /app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080

ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0"

ENTRYPOINT ["/opt/jboss/container/java/run/run-java.sh"]