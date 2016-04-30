FROM java:7

RUN mkdir /data
RUN mkdir /data/rmiclients

COPY rmiclient/ /data/rmiclient/

WORKDIR /data/rmiclient

VOLUME ["/data"]