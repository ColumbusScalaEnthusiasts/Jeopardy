#!/bin/bash

# Usage: run_functional_test <dir> <seconds> <url>
# where:
# <dir>         : directory in which to find the Jeopardy project
# <seconds>     : maximum number of seconds to wait for server to come up
# <url>         : URL to use to feel for the server to be up

get_server_pid () {
	server_pid=$(head -n 1 "$1" | sed 's/.* \([0-9]*\)$/\1/')
}

wait_for_server () {
	exit_code=1
	attempts=0
	while [ $exit_code -ne 0 -a $attempts -lt "$1" ]; do
		curl "$2" > /dev/null
		exit_code=$?
		attempts=$(( attempts + 1 ))
		sleep 1
	done
	return $exit_code
}

cd "$1"
activator stage
game/target/universal/stage/bin/game >functional_test_log.txt &
wait_for_server "$2" "$3"
if [ $? -ne 0 ] ; then
	get_server_pid "functional_test_log.txt"
	kill -9 $server_pid
	exit -1
fi
sbt functional/test
exit_code=$?
get_server_pid "functional_test_log.txt"
kill $server_pid
exit $exit_code

