# builder1
FROM openjdk:8 as builder1

# update and install dependancies
RUN apt-get -y update && apt-get -y install maven

# copy assets
WORKDIR /build
COPY src ./src
COPY pom.xml ./

# build the tartet WAR file
RUN mvn clean package

# base image
#FROM jetty:9.4.9-jre8-alpine
FROM jetty:9-jdk8-slim

# so we can install new packages
USER root
RUN apt-get -y update && apt-get -y install imagemagick-6.q16

# the default user for this base image
USER jetty

# port and run command
EXPOSE 8080

# copy the WAR file
COPY --from=builder1 /build/target/fupload-1.0.war /var/lib/jetty/webapps/ROOT.war

#
# end of file
#
