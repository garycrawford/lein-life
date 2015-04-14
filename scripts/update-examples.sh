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

  lein new rents $1 $2 --db mongodb
}

create_new example-site site
create_new example-api api
