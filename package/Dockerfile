# builder
FROM openjdk:8 as builder

# update and install dependancies
RUN apt-get -y update && apt-get -y install maven

# copy assets
WORKDIR /build
COPY src ./src
COPY pom.xml ./

# build the tartet WAR file
RUN mvn clean package

# base image
FROM jetty:9.4.9-jre8-alpine

# so we can install new packages
USER root

# install updates and dependancies
RUN apk update && apk add --no-cache --repository http://dl-cdn.alpinelinux.org/alpine/v3.3/main/ imagemagick=6.9.6.8-r0

# the default user for this base image
USER jetty

# port and run command
EXPOSE 8080

# copy the WAR file
COPY --from=builder /build/target/fupload-1.0.war /var/lib/jetty/webapps/ROOT.war

#
# end of file
#
