# =========================
# Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY . .

# Build project
RUN mvn clean package -DskipTests

# =========================
# Runtime stage
# =========================
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.24

ENV LANGUAGE='en_US:en'

WORKDIR /deployments

COPY --from=builder /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder /app/target/quarkus-app/*.jar /deployments/
COPY --from=builder /app/target/quarkus-app/app/ /deployments/app/
COPY --from=builder /app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080

USER 185

ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT ["/opt/jboss/container/java/run/run-java.sh"]