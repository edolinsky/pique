[![Build Status](https://travis-ci.org/pique-media/pique.svg?branch=master)](https://travis-ci.org/pique-media/pique)

#Pique
###Intelligent Social Media aggregation

Pique is an automated engine for gathering, sorting, and displaying trending web content.
It is designed to streamline content consumption, allowing users to access top and trending content from multiple social media platforms quickly.

Pique is built on the [Play Framework](https://www.playframework.com/), and leverages [Redis](redis.io) and [Protocol Buffers](https://developers.google.com/protocol-buffers/) for incredibly fast data access.

# Cloning the Pique Repo

Clone the master branch (or, if you're feeling particularly adventurous, clone a feature branch) at [The Pique Repository](https://github.com/pique-media/pique)
using git.

# Setting up Play Framework

Visit the [Play Installation page](https://www.playframework.com/documentation/2.5.x/Installing) and follow the 
instructions detailed there. 

## Downloading Activator

Activator is a very useful build tool, based on the sbt package manager. [Download Activator](https://downloads.typesafe.com/typesafe-activator/1.3.12/typesafe-activator-1.3.12.zip), unzip, and add it 
to your `PATH`.
 
## Using Activator

If you haven't already, [clone the Pique source code](https://github.com/edolinsky/pique), and set up your environment 
variables, as described in the **Environment Variables** section of this document.

Within the `pique` directory in your terminal, you can run a local instance of Pique by issuing
```activator run```
Pique will then start up on `localhost:9000`. You may need to wait a few moments for the Sorter to run for the first 
time before content is available on the webpage.

You can run unit tests by issuing
```activator test```

You can create a production build of Pique for use on an Amazon Web Services Elastic Beanstalk application by running
```activator dist```
The `.zip` package will then be available in the `pique/target/universal/` directory. See [Deploying a Pique Application](https://www.playframework.com/documentation/2.5.x/Deploying) 
for more information.

## Testing in an IDE

If your IDE supports the Play framework or sbt, you should be able to [import the Pique project](https://www.playframework.com/documentation/2.5.x/IDE#Setup-sbteclipse) accordingly. You should 
then be able to run the unit test suite, provided you have imported the required environment variables

# Environment Variables

Environment variables are used for configuring data storage, external API access for data sources, and other things we
need to configure, but don't necessarily want to display on our public repository. If you need them (for grading of this
project, for example) and we haven't supplied them, please ask!

## Application Environment Variables

The following environment variables are required by the Pique application itself:

* `runtime_env` - setting this to `production` will result in Pique using Redis as a data store. Any other field (or
none) will result in Pique using its own 'in memory' data store.

## Content Collector Environment Variables

The following environment variables are required by Pique's content collector classes

### Twitter Collector Environment Variables

* `twitter4j_consumerKey` - Twitter API consumer key
* `twitter4j_consumerSecret` - Twitter API consumer secret
* `twitter4j_accessToken` - Twitter API access token
* `twitter4j_accessTokenSecret` - Twitter API access secret

### Reddit Collector Environment Variables

* `reddit_user` - Reddit username
* `reddit_pass` - Reddit password
* `reddit_client_id` - Reddit client ID
* `reddit_secret` - Reddit client secret

### Imgur Collector Environment Variables

* `imgur_client_id` - Imgur application client ID
* `imgur_client_secret` - Imgur application client secret

## Data Access Environment Variables

When using Redis, the following variables are required:

* `redis_url` - URL of Redis instance
* `redis_port` - Port at `redis_url` on which Redis is listening

## Sorting Node Environment Variables

The sorting node requires the following environment variables to be set:

* `sorting_threshold` - number of posts required to be stored in source channels before the sorting node runs
* `posts_per_page` - number of posts to be stored in each display page

## Production Environment Variables

When running Pique in a production environment (Elastic Beanstalk), the following variables are required:

* `http_port` - http port on which the load balancer listens
* `app_secret` - Secret string (can be any string, but needs to be kept private as the application is not secure if public)

## Test Environment Variables

When _testing_ Redis, the following variables are required:

* `data_source` - set to `redis` to enable Redis tests to run

# Testing Pique

Once you have the Activator tool set up, you should be able to run the tests using `activator test`. This does
require the environment variables mentioned above.

# Deploying Pique

Once you have the Activator tool set up, you should be able to create a deployable package using `activator dist` in 
your command line. This will create a zip package in the `pique/target/universal/` directory that you can upload to an
AWS ElasticBeanstalk application. This requires environment variables, as detailed in the **Environment Variables** 
section.

# Structure

Pique directory structure mainly follows the [Anatomy of a Play! Application](https://www.playframework.com/documentation/2.5.x/Anatomy). 
The application itself is located in `app`, which contains `controllers`, `models`, `filters`, and `services`, where most 
of the backend components are implemented, as well as Scala-templated HTML for display on the browser. 
`javascripts`, `stylesheets`, and `images` passed to the browser are located in `public`. Other directories of interest
are `test`, where unit tests live, `conf`, where configuration files are stored (including `routes.conf` in which the 
HTTP endpoints are routed to specific controllers), and `logs`.
 
# Design Patterns

Play takes most of its design from the Model-View-Controller design pattern, but also makes use of singletons, templates,
builders, observers, data access objects, and possibly some others we may have forgotten about.

