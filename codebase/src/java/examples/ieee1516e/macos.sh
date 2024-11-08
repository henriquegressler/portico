#!/bin/bash

USAGE="usage: macos.sh [compile] [clean] [execute [federate-name]]"

################################
# check command line arguments #
################################
if [ $# = 0 ]
then
	echo $USAGE
	exit;
fi

######################
# test for JAVA_HOME #
######################
JAVA=java
if [ "$JAVA_HOME" = "" ]
then
	echo WARNING Your JAVA_HOME environment variable is not set!
	#exit;
else
        JAVA=$JAVA_HOME/bin/java
fi

#####################
# test for PORTICO_RTI_HOME #
#####################
if [ "$PORTICO_RTI_HOME" = "" ]
then
	cd ../../../
	PORTICO_RTI_HOME=$PWD
	export PORTICO_RTI_HOME
	cd examples/java/ieee1516e
	echo WARNING Your PORTICO_RTI_HOME environment variable is not set, assuming $PORTICO_RTI_HOME
fi

############################################
### (target) clean #########################
############################################
if [ $1 = "clean" ]
then
	echo "deleting example federate jar file and left over logs"
	rm src/ieee1516e/*.class
	rm java-ieee1516e.jar
	rm -Rf logs
	exit;
fi

############################################
### (target) compile #######################
############################################
if [ $1 = "compile" ]
then
	echo "compiling example federate"
	cd src
	javac -cp ./:$PORTICO_RTI_HOME/lib/portico.jar ieee1516e/*.java
	jar -cf ../java-ieee1516e.jar ieee1516e/*.class
	cd ../
	exit;	
fi

############################################
### (target) execute #######################
############################################
if [ $1 = "execute" ]
then
	shift;
	java -cp ./java-ieee1516e.jar:$PORTICO_RTI_HOME/lib/portico.jar ieee1516e.ExampleFederate $*
	exit;
fi

echo $USAGE

