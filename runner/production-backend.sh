#!/usr/bin/bash

# Run the production backend server

fuser -k 8999/tcp || true
source production-backend/env.txt
java -jar production-backend/libs/backend-0.0.1-SNAPSHOT.jar --server.port=8999
