# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /app
COPY pom.xml .
# Descargamos dependencias primero para cachear esta capa
RUN mvn dependency:go-offline
COPY src ./src
# Compilamos el proyecto omitiendo los tests para mayor velocidad
RUN mvn clean package -DskipTests

# Etapa 2: Producción (Run)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copiamos el archivo .jar generado en la Etapa 1
COPY --from=builder /app/target/gutti-0.0.1-SNAPSHOT.jar app.jar
# Exponemos el puerto 8080
EXPOSE 8080
# Comando final para arrancar el servidor
ENTRYPOINT ["java", "-jar", "app.jar"]
