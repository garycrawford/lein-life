# Life

<img src="https://raw.githubusercontent.com/garycrawford/lein-life/master/images/binary_renton.png" alt="Binary Renton" title="Renton from Trainspotting" align="right" />

> _We start off with high hopes, then we bottle it. We realise
> that we’re all going to die, without really finding out the
> big answers. We develop all those long-winded ideas which just
> interpret the reality of our lives in different ways, without
> really extending our body of worthwhile knowledge, about the big
> things, the real things. Basically, we live a short disappointing life;
> and then we die._
> - Mark Renton, Trainspotting by Irvine Welsh

Life is a Leiningen template to give your project a better chance in life than Mark Renton had! It utilises Docker to give you a production like environment running locally on your dev machine where things like metrics and logging now become first class citizens of you dev lifecycle.


# Usage
[![Build Status](https://snap-ci.com/garycrawford/lein-life/branch/master/build_image)](https://snap-ci.com/garycrawford/lein-life/branch/master)

A video demonstrating how to use Life can be found [here on YouTube](https://youtu.be/ZloJuOiqA6w).

_Caveat - heavy development underway... functionality being added rapidly and changes made regularly._

## Dependencies
To use Life generated projects to their full potential in a dev environment you will need to install Docker and Docker Compose.

## Creating projects
You can use this template by executing:

    $ lein new life <project-name> <type> [options]

    Types:
      api      | Create a new web api
      site     | Create a new site
    
    Options:
      -d, --db        | DATABASE  | Database to be used. Supports `mongodb` for api or `mongodb` & `api` for site
      -i, --docker-ip | DOCKER_IP | IP address for Docker or boot2docker (see Finding the Docker IP below)
      -s, --site-name | SITE-NAME | Name of the site project
      -a, --api-name  | API-NAME  | Name of the api project
      -h, --help      |           |


## Finding the Docker IP
If you are using a Linux distro you can get the IP from the "docker0" virtual interface by executing:

    $ ifconfig

If you are using a Mac with boot2docker you can simply execute:

    $ boot2docker ip
    
## Launching the ecosystem
You can then launch the generated app or site by executing:

    $ cd <project-name>
    $ docker-compose up

I find that the Text Triumvirate (zsh, vim & tmux) gives me everything I need for effective Clojure development with Life (see my setup below) however templates should be compatible with the IDE & shell combo of your choice.

<img src="https://raw.githubusercontent.com/garycrawford/lein-life/master/images/IDE.png" alt="Tmux and Vim" title="Tmux and Vim for Clojure development" />

## Working with a Life generated project
### Get REPL to api or site
When you connect to a REPL in a Life project the REPL is actually running inside a Docker container, so you have to do a remote connection to it:

    ;; site
    $ lein repl :connect $DOCKER_IP:21212
    
    ;; api
    $ lein repl :connect $DOCKER_IP:31313
    
### Enable autotest

    ;; enabling autotest in repl will rerun tests when chnages are saved
    user=> (autotest)
    
### Start site or API

    ;; start the API or site
    user=> (go)
    
    ;; refresh changes you have made
    user=> (reset)
    
    ;; stop the API or site
    user=> (stop)
    
### Viewing metrics
The screenshot below shows the default metrics available as soon as you start your Life project.

<img src="https://raw.githubusercontent.com/garycrawford/lein-life/master/images/stats.png" alt="Grafana stats" title="Screenshot of Grafana stats" />

To see these simply browse to http://$DOCKER_IP. (N.B. so far this is only tested on a Mac using boot2docker). You will start to see stats once you start your 'site' or 'API' (see above).

N.B. boot2docker has a clock skew issue - if you cannot see metrics you should try running the following command on the host:

    $ boot2docker ssh sudo ntpclient -s -h pool.ntp.org
