#!/usr/bin/bash

# Run the staging backend server

fuser -k 9499/tcp || true
source staging-backend/env.txt
java -jar staging-backend/libs/backend-0.0.1-SNAPSHOT.jar --server.port=9499
