[![Build Status](https://travis-ci.org/pique-media/pique.svg?branch=master)](https://travis-ci.org/pique-media/pique)

#Pique
###Intelligent Social Media aggregation

Pique is an automated engine for gathering, sorting, and displaying trending web content.
It is designed to streamline content consumption, allowing users to access top and trending content from multiple social media platforms quickly.

Pique is built on the [Play Framework](https://www.playframework.com/), and leverages [Redis](redis.io) and [Protocol Buffers](https://developers.google.com/protocol-buffers/) for incredibly fast data access.

Check out how pique was designed and built in our [Wiki!](https://github.com/edolinsky/pique/wiki)

##Cloning the Pique Repo

Clone the master branch (or, if you're feeling particularly adventurous, clone a feature branch) at [The Pique Repository](https://github.com/pique-media/pique)
using git.

##Setting up Play Framework

Visit the [Play Installation page](https://www.playframework.com/documentation/2.5.x/Installing) and follow the 
instructions detailed there. 
Don't forget to add the executable to your path!

If you haven't already, [clone the Pique source code](https://github.com/edolinsky/pique). You should then be able to 
start your local instance of Pique by executing `activator run` in the pique-java directory.

##Environment Variables

Environment variables are used for configuring data storage, external API access for data sources, and other things we
need to configure, but don't necessarily want to display on our public repository. If you need them (for grading of this
project, for example) and we haven't supplied them, please ask!

Expected environment variables can be found in the static class `app.services.PublicConstants`.

##Testing Pique

Once you have the Activator executable set up, you should be able to run the tests using `activator test`. This does
require the environment variables mentioned above.

##Structure

Pique directory structure mainly follows the [Anatomy of a Play! Application](https://www.playframework.com/documentation/2.5.x/Anatomy). 
The application itself is located in `app`, which contains `controllers`, `models`, `filters`, and `services`, where most 
of the backend components are implemented, as well as Scala-templated HTML for display on the browser. 
`javascripts`, `stylesheets`, and `images` passed to the browser are located in `public`. Other directories of interest
are `test`, `conf`, and `logs`.
 
##Design Patterns

Play takes most of its design from the Model-View-Controller design pattern, but also makes use of singletons, templates,
builders, observers, data access objects (and possibly some others we may have forgotten).
