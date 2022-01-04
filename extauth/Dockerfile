# the first stage of our build will use a maven parent image
FROM maven:3.6.1-alpine AS MAVEN_BUILD

# copy the pom and src code to the container
COPY ./ ./

# package our application code
RUN mvn clean package

# the second stage of our build will use open jdk 8 on alpine
FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD target/extauth-*.jar /extauth.jar

# set the startup command to execute the jar
CMD ["java", "-jar", "/extauth.jar"]