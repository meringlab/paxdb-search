This is the [pax-db.org][http://pax-db.org] protein search microservice.

# Installation

# Versioning

All versions are `<major>.<minor>.<patch>`, where major and minor follow
[pax-db.org](pax-db.org) versions.


# License

MIT. See "LICENSE.txt".

## Usage
### build the image
`docker build . -t paxdb5_0`

### run container in interactive mode and build index into /data folder
```
docker run -it --network=host --user=root --restart=always --name paxdb_search_lucene_index_ct --entrypoint /bin/bash paxdb5_0

cd /srv/paxdb && mvn -P build-index install
```

### copy built index into host directory to be served
`docker cp $containerID:/data/lucene_index_5_0 [host_data_directory]`

### serve the paxdb-search API
`docker run -it --user=paxdb --restart=always -p 127.0.0.1:[host_port]:[docker_port]/tcp --name [] --entrypoint /bin/bash -v [host_data_directory]:/data/lucene_index_5_0 paxdb5_0`

## Update procedure

The process of updating to a new version is as follows:

1. update versions in pom.xml and update other parameters in paxdb.properties (possibly also hibernate.properties)
2. sql schema with tables `species`, `proteins`, `proteins_names` in localhost
3. global replace with new sql schema (e.g. paxdb5_0)
4. global replace /opt/paxdb/[version_no] with new [version_no]

