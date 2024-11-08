#!/bin/bash

USAGE="usage: macos.sh [compile] [clean] [debug] [execute [federate-name]]"

################################
# check command line arguments #
################################
if [ $# = 0 ]
then
	echo $USAGE
	exit;
fi

###################
# Set up PORTICO_RTI_HOME #
###################
cd ../../..
PORTICO_RTI_HOME=$PWD
export PORTICO_RTI_HOME
cd examples/cpp/hla13
echo PORTICO_RTI_HOME environment variable is set to $PORTICO_RTI_HOME

############################################
### (target) clean #########################
############################################
if [ $1 = "clean" ]
then
	echo "deleting example federate executable and left over logs"
	rm example-federate
	rm -Rf logs
	exit;
fi

############################################
### (target) compile #######################
############################################
if [ $1 = "compile" ]
then
	echo "compiling example federate"
	g++ -O1 -fPIC -I$PORTICO_RTI_HOME/include/hla13 \
	    -g \
	    -DRTI_USES_STD_FSTREAM \
		-lRTI-NG_64d -lFedTime_64d -L$PORTICO_RTI_HOME/lib/gcc4 \
		main.cpp ExampleCPPFederate.cpp ExampleFedAmb.cpp -o example-federate
	exit;
fi

############################################
### (target) debug #########################
############################################
if [ $1 = "debug" ]
then
	echo "starting ggdb - we need to sudo to avoide a bunch of code-signing stuff - sorry :("
	sudo ggdb -x gdb-macos.env ./example-federate
	exit;
fi

############################################
### (target) execute #######################
############################################
if [ $1 = "execute" ]
then
	shift;
	DYLD_LIBRARY_PATH="$PORTICO_RTI_HOME/lib/gcc4:$PORTICO_RTI_HOME/jre/lib/server" ./example-federate $*
	exit;
fi

echo $USAGE

