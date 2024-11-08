#!/bin/bash

USAGE="usage: linux64.sh [compile] [clean] [execute [federate-name]]"

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
	g++ -g -O1 -fPIC -I$PORTICO_RTI_HOME/include/hla13 \
	    -DRTI_USES_STD_FSTREAM \
		main.cpp ExampleCPPFederate.cpp ExampleFedAmb.cpp -o example-federate \
		-L$PORTICO_RTI_HOME/lib/gcc8 -lRTI-NG64 -lFedTime64 \
		-L$JAVA_HOME/lib/server -ljvm -ljsig
	exit;
fi

############################################
### (target) debug #########################
############################################
if [ $1 = "debug" ]
then
	echo "starting gdb"
	gdb -x gdb-linux.env ./example-federate
	exit;
fi

############################################
### (target) execute #######################
############################################
if [ $1 = "execute" ]
then
	shift;
	LD_LIBRARY_PATH="$PORTICO_RTI_HOME/lib/gcc8:$JAVA_HOME/lib/server" ./example-federate $*
	exit;
fi

echo $USAGE

