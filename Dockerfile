# sudo docker build -t paxdb/search .
# sudo docker run -d -P --restart=always --name paxdb-search -v /tmp/lucene_index_4:/data/lucene_index_4 paxdb/search
#

FROM        meringlab/java8
MAINTAINER  Milan Simonovic <milan.simonovic@imls.uzh.ch>

# TODO create user

RUN apt-get update && apt-get -y install maven

ADD . /srv/paxdb/
ADD paxdb.properties hibernate.properties /opt/paxdb/v4.0/

WORKDIR /srv/paxdb

RUN mvn	install

EXPOSE 9095

WORKDIR /srv/paxdb/webservice-war

VOLUME ["/data"]

# Command to run
#ENTRYPOINT ["/scripts/run.sh"]
#CMD [""]


# TODO create a script that accepts params and either starts jetty or runs indexer
CMD ["mvn", "jetty:run-war"]
# to index run: mvn -P build-index install



ENV SERVICE_TAGS "paxdb,api"
ENV SERVICE_NAME paxdb_search_api_v4.0
