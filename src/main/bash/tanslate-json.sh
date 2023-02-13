#!/usr/bin/env bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
	BIN_DIR="$(cd -P "$(dirname "$SOURCE")" > /dev/null 2>&1 && pwd)"
	SOURCE="$(readlink "$SOURCE")"
	[[ $SOURCE != /* ]] && SOURCE="$BIN_DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done

BIN_DIR="$(cd -P "$(dirname "$SOURCE")" > /dev/null 2>&1 && pwd)"
PROJECT_DIR=$(realpath "$BIN_DIR"/../../..)


if ! type -p java > /dev/null 2>&1; then
	echo "java not found";
	exit 1;
fi

JAR=$(ls $PROJECT_DIR/target/translate-api-*-jar-with-dependencies.jar 2> /dev/null | head -1)
if [ -z "$JAR" ]; then
	echo "jar not found, build with maven" 
	if ! type -p mvn > /dev/null 2>&1; then
		echo "mvn not found. Can't build project";
		exit 1;
	fi
	
	mvn -f $PROJECT_DIR/pom.xml -Dmaven.test.skip=true package
	JAR=$(ls $PROJECT_DIR/target/libretranslate-api-*-jar-with-dependencies.jar 2> /dev/null | head -1)
	if [ -z "$JAR" ]; then
		exit 1;
	fi
fi

if [ $DEBUG ]; then
	DEBUG_AGENT=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5001
fi 

#OPTS=-Djdk.httpclient.HttpClient.log=requests,headers,body,content,frames[:control:data:window:all],content,trace,channel,all

echo $JAR
java $DEBUG_AGENT $OPTS -jar $JAR translate-json $*

