
# uploadPackage.sh
~~~bash
#!/usr/bin/env bash
# eg. sh uploadPackage.sh bitnami@3.38.63.165

REMOTE=$1
scp -r ../../bsin-server-targe-gateway/target/bsin-server-targe-gateway-1.0.0-SNAPSHOT.jar $REMOTE:~/copilot/server/
scp -r ../../bsin-server-upms/upms-server/target/upms-server-1.0.0-SNAPSHOT.jar $REMOTE:~/copilot/server/
scp ./startGateway.sh $REMOTE:~/copilot/server/

~~~

# startGateway.sh
~~~bash
#!/usr/bin/env bash
nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m bsin-server-targe-gateway-1.0.0-SNAPSHOT.jar >./logs/gateway.log &
nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m upms-server-1.0.0-SNAPSHOT.jar >./logs/upms.log &
~~~