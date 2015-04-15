# Lein rents

<img src="https://raw.githubusercontent.com/garycrawford/lein-rents/master/images/binary_rents.png"
alt="Binary Rents" title="Renton from Trainspotting" align="right" />


# Usage
[![Build Status](https://snap-ci.com/garycrawford/lein-rents/branch/master/build_image)](https://snap-ci.com/garycrawford/lein-rents/branch/master)

A Leiningen template which produces a docker ready site or api with embedded Jetty web-server, Graphite/Grafanna instrumentation and many customisations.


You can use this template by executing:

    $ lein new rents <project-name> <type> [options]

Types:
* api      Create a new web api
* site     Create a new site

You can then launch the app by:

    $ cd <project-name>
    $ docker-compose up

