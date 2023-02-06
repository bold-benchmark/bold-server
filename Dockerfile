# Dockerfile to build and run BOLD
#
# sudo docker build . -t bold-server
# sudo docker run -p 8080:8080 -it bold-server
# sudo docker run -p 8080:8080 -e TASKNAME=ts1 -it bold-server

# Build BOLD
FROM gradle:6-jdk8-focal AS build

RUN mkdir /opt/bold-build

COPY src             /opt/bold-build/src

COPY build.gradle    /opt/bold-build/
COPY settings.gradle /opt/bold-build/
COPY gradle*         /opt/bold-build/

COPY data            /opt/bold-build/data
COPY query           /opt/bold-build/query
COPY *properties     /opt/bold-build/

WORKDIR /opt/bold-build

RUN gradle install


# Run BOLD
FROM eclipse-temurin:8

ENV TASKNAME=""

COPY --from=0 /opt/bold-build/build /opt/bold

WORKDIR /opt/bold/install/bold-server

CMD bin/bold-server ${TASKNAME}

