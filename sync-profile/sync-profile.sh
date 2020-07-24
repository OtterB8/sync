#!/bin/bash

#run services
docker-compose -f cluster.yaml up --build -d

# register services
node ./service-register/index.js