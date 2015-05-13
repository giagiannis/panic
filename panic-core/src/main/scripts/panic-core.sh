#!/bin/bash
#
# 
# author: ggian
# date: Wed May 13 11:57:19 EEST 2015
# 
# description: this script is used to debug panic core algorithms



# classpath extraction
export CLASSPATH=$(echo target/lib/* | tr ' ' ':')
export JAR=$(echo target/*.jar)
[ "$CLASS" == "" ] && export CLASS="gr.ntua.ece.cslab.panic.core.samplers.special.PrincipalComponentsSampler"
java -cp $CLASSPATH:$JAR $CLASS  $@
