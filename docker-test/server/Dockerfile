FROM java:7

RUN mkdir /data
RUN mkdir /data/rmiserver

COPY rmiserver/ /data/rmiserver/

WORKDIR /data/rmiserver

VOLUME ["/data"]