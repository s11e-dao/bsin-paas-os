#!/bin/bash

mvn install:install-file -Dfile=../doc/libs/web3j/abi-0.3.0.jar -DgroupId=org.tron.trident -DartifactId=abi -Dversion=0.3.0 -Dpackaging=jar
mvn install:install-file -Dfile=../doc/libs/web3j/core-0.3.0.jar -DgroupId=org.tron.trident -DartifactId=core -Dversion=0.3.0 -Dpackaging=jar
mvn install:install-file -Dfile=../doc/libs/web3j/utils-0.3.0.jar -DgroupId=org.tron.trident -DartifactId=utils -Dversion=0.3.0 -Dpackaging=jar
mvn install:install-file -Dfile=../doc/libs/web3j/conflux-web3j-1.2.6.jar -DgroupId=io.github.conflux-chain -DartifactId=conflux.web3j -Dversion=1.2.6 -Dpackaging=jar
