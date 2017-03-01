# sudo docker build -t paxdb/search .
# sudo docker run -d -P --restart=always --name paxdb-search -v /tmp/lucene_index_4:/data/lucene_index_4 paxdb/search
#

FROM        meringlab/java8
MAINTAINER  Milan Simonovic <milan.simonovic@imls.uzh.ch>

ENV SERVICE_TAGS "paxdb,api"
ENV SERVICE_NAME paxdb_search_api_v4.0


RUN apt-get update && apt-get -y install maven


ADD . /srv/paxdb/
ADD paxdb.properties hibernate.properties /opt/paxdb/v4.0/

WORKDIR /srv/paxdb

RUN mvn	install

EXPOSE 9095

WORKDIR /srv/paxdb/webservice-war

VOLUME ["/data"]

ENTRYPOINT ["/srv/paxdb/run.sh"]
CMD [""]
# to index pass 'index' as CMD
