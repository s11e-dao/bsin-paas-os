#!/bin/bash

mvn install:install-file -Dfile=../doc/libs/ipfs/multibase-v1.1.0.jar -DgroupId=com.github.multiformats -DartifactId=java-multibase -Dversion=v1.1.0 -Dpackaging=jar
mvn install:install-file -Dfile=../doc/libs/ipfs/java-ipfs-api-1.3.3.jar -DgroupId=com.github.ipfs -DartifactId=java-ipfs-api -Dversion=v1.3.3 -Dpackaging=jar
mvn install:install-file -Dfile=../doc/libs/ipfs/multiaddr-v1.4.0.jar -DgroupId=com.github.multiformats -DartifactId=java-multiaddr -Dversion=v1.4.0 -Dpackaging=jar
mvn install:install-file -Dfile=../doc/libs/ipfs/multihash-v1.3.0.jar -DgroupId=com.github.multiformats -DartifactId=java-multihash -Dversion=v1.3.0 -Dpackaging=jar
mvn install:install-file -Dfile=../doc/libs/ipfs/cid-v1.3.0.jar -DgroupId=com.github.ipld -DartifactId=java-cid -Dversion=v1.3.0 -Dpackaging=jar

