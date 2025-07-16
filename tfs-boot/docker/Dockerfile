FROM tx-harbor.smyoa.com/library/smy-jdk:2.2.0
MAINTAINER lizeliang@smyfinancial.com
WORKDIR /app/smy-tfs/jetty/tfs-boot
RUN  mkdir -p /app/scm/config && mkdir /app/scm/data && mkdir -p /data/applogs/cat
RUN  mkdir -p /app/smy-tfs/jetty/tfs-boot/
COPY docker/start.sh ./bin/
COPY docker/stop.sh ./bin/
COPY docker/service.properties /app/scm/config/
COPY lib/  ./lib/
COPY conf/ ./conf/
RUN chmod a+x ./bin/start.sh;\
    chmod a+x ./bin/stop.sh;\
    groupadd coreapp && useradd -g coreapp smy-tfs;\
    chown -R smy-tfs:coreapp /app /data/applogs/cat
USER smy-tfs
CMD ["./bin/start.sh"]

