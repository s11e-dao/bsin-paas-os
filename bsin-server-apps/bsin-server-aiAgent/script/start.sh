nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m eco-register.jar >register.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m eco-auth.jar >auth.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m eco-upms-biz.jar >upms-biz.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m eco-yjsh-portal.jar >portal.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m eco-yjsh-cashier-desk.jar >desk.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m eco-gateway.jar >gateway.out &


nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m unimall-launcher-0.0.1-RELEASE.jar --spring.profiles.active=prd >unimall.out &


nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m server-eureka.jar >eureka.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m server-config.jar >config.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m system-service.jar >system.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m course-service.jar >course.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m server-sba.jar >sba.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m user-service.jar >user.out &

nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m server-gateway.jar >gateway.out &


nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m eco-yjsh-portal-prod.jar >portal.out &


nohup java -jar -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512m eco-register-prod.jar >register.out &


nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m upms-server-1.0.0-SNAPSHOT.jar >upms.out &


nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m bsin-server-targe-gateway-1.0.0-SNAPSHOT.jar >gate.out &


nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m blockchain-server-1.0.0-SNAPSHOT.jar >blockchain.out &


nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m scaffold-server-1.0.0-SNAPSHOT.jar >car.out &


nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m huoyuanshequ-server-1.0.0-SNAPSHOT.jar >huoyuanshequ.out &


nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m web3-ability-server-1.0.0-SNAPSHOT.jar >web3.out &

nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m workflow-server-2.0.0-SNAPSHOT.jar >workflow.out &


nohup java -jar -Xms1024m -Xmx2048m  -XX:PermSize=512M -XX:MaxPermSize=1024m workflow-admin-server-2.0.0-SNAPSHOT.jar >adminWorkflow.out &

wget https://npm.taobao.org/mirrors/node/v14.19.1/node-v14.19.1-linux-x64.tar.gz


./seata-server.bat -p 8091 -h 127.0.0.1 -m file

./seata-server.sh -p 8091 -h 127.0.0.1 -m file &


mvn install:install-file -Dfile=C:\\Users\\bolei\\Downloads\\multibase.jar -DgroupId=com.github.multiformats -DartifactId=java-multibase -Dversion=v1.1.0 -Dpackaging=jar



mvn install:install-file -Dfile=E:\\block-workspace\\trident\\trident-java\\utils\\build\\libs\\core-0.3.0.jar -DgroupId=org.tron.trident -DartifactId=core -Dversion=0.3.0 -Dpackaging=jar


mvn install:install-file -Dfile=E:\\block-workspace\\java-conflux-sdk-log\\build\\libs\\conflux-web3j-1.2.6.jar -DgroupId=io.github.conflux-chain -DartifactId=conflux.web3j -Dversion=1.2.6 -Dpackaging=jar

mvn package -Dmaven.test.skip=true


mvn install:install-file -Dfile=C:\\Users\\bolei\\Downloads\\multibase.jar -DgroupId=com.github.multiformats -DartifactId=java-multibase -Dversion=v1.1.0 -Dpackaging=jar


mvn install:install-file -Dfile=E:\\项目\\私募数据管理平台\\软件包\\besmq-2.1.0-spring-boot-starter-2.x\\besmq-2.1.0-spring-boot-starter-2.x\\besmq-spring-boot-2.x-starter-2.1.0.jar -DgroupId=com.bes.mq -DartifactId=spring-boot-starter-besmq -Dversion=2.1.0 -Dpackaging=jar


 
 java -jar besmq-spring-boot-2.x-starter-producer-test.jar --spring.besmq.broker-url="tcp://10.239.0.110:3201"  --spring.besmq.user="strategy" --spring.besmq.password="strategy"


 http://localhost:8088/sendToTopic?topicName=privateFundTopic&num=20&msg=123


 编写bsin-paas的docker部署手册
 sofa版本升级
 
