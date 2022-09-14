# sudo docker build -t paxdb/search .
# sudo docker run -d -P --restart=always --name paxdb-search -v /tmp/lucene_index_4:/data/lucene_index_4 paxdb/search
#

FROM        meringlab/java8
LABEL maintainer="qingyao.huang@uzh.ch"

ENV SERVICE_TAGS "paxdb,api"
ENV SERVICE_NAME paxdb_search_api_v5.0

RUN apt-get update && apt-get -y install maven
RUN apt-get install -y vim
RUN useradd -ms /bin/bash paxdb

EXPOSE 9095
VOLUME ["/data"]

ADD . /srv/paxdb/
ADD paxdb.properties hibernate.properties /opt/paxdb/v5.0/
RUN chown -R paxdb /srv/paxdb/ /opt/paxdb/ /data/ 
WORKDIR /srv/paxdb

USER paxdb
RUN mvn install
WORKDIR /srv/paxdb/webservice-war

# ENTRYPOINT ["/bin/bash"] 
ENTRYPOINT ["/srv/paxdb/run.sh"]
CMD [""]
# to index pass 'index' as CMD
