#!/usr/bin/env bash

##
# Use this to generate a new sample version of the 'site' or 'api' from the plugin.
# 
# Author: Gary Crawford
##

set -o errexit
set -o nounset

lein install

create_new () {
  if [ -d "./$1" ]; then
  	rm -rf "./$1"
  fi

  lein new life $1 $2
}


if [ -d "./example-site" ]; then
	rm -rf "./example-site"
fi
lein new life example-site site -i 192.168.59.103


if [ -d "./example-api" ]; then
	rm -rf "./example-api"
fi
lein new life example-api api -i 192.168.59.103


if [ -d "./example-both" ]; then
	rm -rf "./example-both"
fi
lein new life example-both site --db api -i 192.168.59.103
