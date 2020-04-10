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
FROM tomcat:8.0

# install updates and dependancies
RUN apt-get -y update

# add the healthcheck
RUN mkdir /usr/local/tomcat/webapps/healthcheck && echo "OK" > /usr/local/tomcat/webapps/healthcheck/index.html

# port and run command
EXPOSE 8080

# copy the WAR file
COPY --from=builder /build/target/fupload-1.0.war /usr/local/tomcat/webapps/fupload.war

#
# end of file
#
