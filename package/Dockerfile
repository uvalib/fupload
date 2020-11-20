# builder1
FROM alpine:3.12 as builder1

RUN apk update && apk upgrade && apk add bash wget tar make g++

WORKDIR /build

RUN wget https://www.ece.uvic.ca/~frodo/jasper/software/jasper-1.701.0.zip && unzip jasper-1.701.0
RUN cd jasper-1.701.0 && ./configure && make install
RUN wget https://www.imagemagick.org/download/releases/ImageMagick-6.7.2-10.tar.xz && tar xvf ImageMagick-6.7.2-10.tar.xz
RUN cd ImageMagick-6.7.2-10 && ./configure --enable-static=yes --enable-shared=no && make install

# builder2
FROM openjdk:8 as builder2

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
RUN apk update && apk upgrade && apk add libgomp

# install our built imagemagick
COPY --from=builder1 /usr/local/bin/convert /usr/local/bin/convert

# the default user for this base image
USER jetty

# port and run command
EXPOSE 8080

# copy the WAR file
COPY --from=builder2 /build/target/fupload-1.0.war /var/lib/jetty/webapps/ROOT.war

#
# end of file
#
