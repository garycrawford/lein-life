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

Rents is a Leiningen template to give your project a better environment and chance in life than Mark Renton!


# Usage
[![Build Status](https://snap-ci.com/garycrawford/lein-rents/branch/master/build_image)](https://snap-ci.com/garycrawford/lein-rents/branch/master)

You can use this template by executing:

    $ lein new rents <project-name> <type> [options]


    Types:
      api  | Create a new web api
      site | Create a new site
    
    Options:
      -d, --db   | DATABASE | Database to be used. Currently only supports `mongodb`
      -v         |          | Verbosity level; may be specified multiple times to increase value
      -h, --help |          |


You can then launch the igenerated app or site by executing:

    $ cd <project-name>
    $ docker-compose up

