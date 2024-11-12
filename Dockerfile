# Use a lightweight OpenJDK base image
#FROM bellsoft/liberica-openjdk-alpine
#WORKDIR /app
#COPY build/libs/dremota-all.jar /app/app.jar
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]


# First stage: build the application
FROM bellsoft/liberica-openjdk-alpine AS build
WORKDIR /app
COPY build/libs/dremota-all.jar /app/app.jar

# Second stage: minimal runtime environment
FROM bellsoft/liberica-runtime-container
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
