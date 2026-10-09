FROM ubuntu:latest
LABEL authors="Slavka"

ENTRYPOINT ["top", "-b"]