# 
# build the target application
#
FROM public.ecr.aws/docker/library/amazoncorretto:8-al2022-jdk as builder

# update and install dependancies
RUN yum -y update && yum -y install maven

# copy assets
WORKDIR /build
COPY src ./src
COPY pom.xml ./

# build the target WAR file
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-amazon-corretto.x86_64
RUN mvn clean package

# base image
FROM public.ecr.aws/docker/library/jetty:9.4.50-jre8

# so we can install new packages
USER root
RUN apt-get -y update && apt-get -y install imagemagick-6.q16

# the default user for this base image
USER jetty

# port and run command
EXPOSE 8080

# copy the WAR file
COPY --from=builder /build/target/fupload-1.0.war /var/lib/jetty/webapps/ROOT.war

#
# end of file
#
