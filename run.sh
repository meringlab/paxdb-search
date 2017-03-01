#!/usr/bin/env bash

set -e

if [[ $1 == 'index' ]]
then
    cd .. && mvn -P build-index install
else
    mvn jetty:run-war
fi
