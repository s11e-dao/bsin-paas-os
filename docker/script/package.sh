#!/bin/bash

echo "1. build frontend start..."

echo "1.1. build bsin-apps-container"
cd ../bsin-apps-container
yarn build

echo "1.2. build bsin-ui-ai-agent"
cd ../bsin-ui-apps/bsin-ui-ai-agent
yarn build

echo "1.3. build bsin-ui-decision-admin"
cd ../bsin-ui-decision-admin
yarn build

echo "1.4. build bsin-ui-upms"
cd ../bsin-ui-upms
yarn build

echo "1.5. build bsin-ui-waas"
cd ../bsin-ui-waas
yarn build


echo "2. build server start..."


