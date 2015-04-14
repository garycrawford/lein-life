#!/usr/bin/env bash

##
# Runs tests within the context of the path provided.
# 
# Author: Gary Crawford
##

set -o errexit
set -o nounset

proj_path=$1

if [ -d $proj_path ]; then
	cd $proj_path
	lein midje
fi
