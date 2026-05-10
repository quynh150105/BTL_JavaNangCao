# =========================
# Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy toàn bộ source code
COPY . .

# Cấp quyền cho mvnw
RUN chmod +x mvnw

# Build project
RUN ./mvnw clean package -DskipTests

# =========================
# Runtime stage
# =========================
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.24

ENV LANGUAGE='en_US:en'

WORKDIR /deployments

# Copy file build từ stage builder
COPY --from=builder /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder /app/target/quarkus-app/*.jar /deployments/
COPY --from=builder /app/target/quarkus-app/app/ /deployments/app/
COPY --from=builder /app/target/quarkus-app/quarkus/ /deployments/quarkus/

# Port ứng dụng
EXPOSE 8080

# User mặc định của image RedHat
USER 185

# Config chạy Quarkus
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

# Start app
ENTRYPOINT ["/opt/jboss/container/java/run/run-java.sh"]