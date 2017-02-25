# sudo docker build -t paxdb/search .
# sudo docker run -d -P --restart=always --name paxdb-search -v /tmp/lucene_index_4:/data/lucene_index_4 paxdb/search
#

FROM        meringlab/java8
MAINTAINER  Milan Simonovic <milan.simonovic@imls.uzh.ch>

# TODO create user

RUN apt-get update && apt-get -y install maven

ADD . /srv/paxdb/
ADD paxdb.properties /opt/paxdb/v4.0/paxdb.properties

WORKDIR /srv/paxdb

RUN mvn	install

EXPOSE 9095

WORKDIR /srv/paxdb/webservice-war

# Command to run
#ENTRYPOINT ["/scripts/run.sh"]
#CMD [""]
CMD ["mvn", "jetty:run-war"]

ENV SERVICE_TAGS "paxdb,api"
ENV SERVICE_NAME paxdb_search_api_v4.0
