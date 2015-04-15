# Lein Rents

<img src="https://raw.githubusercontent.com/garycrawford/lein-rents/master/images/binary_rents.jpg"
alt="Binary Rents" title="Renton from Trainspotting" align="right" />

> We start off with high hopes, then we bottle it. We realise
> that we’re all going to die, without really finding out the
> big answers. We develop all those long-winded ideas which just
> interpret the reality of our lives in different ways, without
> really extending our body of worthwhile knowledge, about the big
> things, the real things. Basically, we live a short disappointing life;
> and then we die.
> - Mark Renton, Trainspotting by Irvine Welsh


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

