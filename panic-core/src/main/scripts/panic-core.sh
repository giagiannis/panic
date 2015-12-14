#!/bin/bash
#
# 
# author: ggian
# date: Wed May 13 11:57:19 EEST 2015
# 
# description: this script is used to debug panic core algorithms


# classpath extraction
export CLASSPATH="$(echo ${PWD}/target/lib/* | tr ' ' ':')"
export CLASSPATH="$CLASSPATH:$(echo ${PWD}/target/conf)"
export JAR="target/classes"

[ "$CLASS" == "" ] && export CLASS="gr.ntua.ece.cslab.panic.core.client.Main"

java -cp $CLASSPATH:$JAR $CLASS  $@
