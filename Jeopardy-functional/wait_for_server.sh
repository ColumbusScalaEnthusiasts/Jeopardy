#!/bin/bash

# Usage: wait_for_server.sh <max seconds to wait> <url to request>

exit_code=1
attempts=0

while [ $exit_code -ne 0 -a $attempts -lt "$1" ]; do
	curl "$2" > /dev/null
	exit_code=$?
	attempts=$(( attempts + 1 ))
	sleep 1
done

exit $exit_code

