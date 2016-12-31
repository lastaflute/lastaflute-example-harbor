#!/bin/bash

cd `dirname $0`
. ./_project.sh

FIRST_ARG=$1
SECOND_ARG=$2

export DBFLUTE_ENVIRONMENT_TYPE=integration

sh $DBFLUTE_HOME/etc/cmd/_df-manage.sh $MY_PROPERTIES_PATH $FIRST_ARG $SECOND_ARG
taskReturnCode=$?

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "Remove the environment type (closing)."
echo "nnnnnnnnnn/"
unset DBFLUTE_ENVIRONMENT_TYPE

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
