FROM        meringlab/oracle-java8
MAINTAINER  Milan Simonovic <milan.simonovic@imls.uzh.ch>

# TODO create user

ADD . /srv/paxdb/
ADD paxdb.properties /opt/paxdb/v4.0/paxdb.properties

CWD /srv/paxdb/webservice-war

EXPOSE 9095

# Command to run
#ENTRYPOINT ["/scripts/run.sh"]
#CMD [""]
CMD ["mvn", "jetty:run-war"]

ENV SERVICE_TAGS "paxdb,api"
ENV SERVICE_NAME paxdb_search_api_v4.0
