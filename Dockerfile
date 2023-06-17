# Use the official Maven image as the base image for building
FROM maven:3.9.2-amazoncorretto-20 AS builder

# Set the working directory in the container
WORKDIR /codequest

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the project source code
COPY src ./src

# Build the application
RUN mvn clean compile assembly:single

# Use Amazon Corretto as the base image for the runtime
FROM amazoncorretto:20-alpine

RUN apk add socat
RUN apk add python3

# Set the working directory in the container
WORKDIR /codequest

# Copy the JAR file from the builder stage
COPY --from=builder /codequest/target/codequest23-1.0-SNAPSHOT-jar-with-dependencies.jar ./app.jar

# Copy run.sh
COPY run.sh /codequest
RUN chmod +x /codequest/run.sh

# Run the Java application
CMD ["/bin/sh", "-c", "./run.sh"]
