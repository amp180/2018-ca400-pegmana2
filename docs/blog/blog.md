# Blog: Detecting brigading on social media

**Adam Pegman**

One blog post per task in the functional spec.

## Setting up a java build 

I have set up a springboot project, with a gradle build file to manage the dependencies and compile the Java files,
as well as compiling sass files to css for the frontend and running JUnit tests. 

## Create DB Schema

I have added sqlite and mybatis as dependencies for the project, and fleshed out the Database schema from the functional spec.
challenges when fleshing out the schema included making sure that the statistics could be cached, but were also stored independently from the comments and words, 
in case I need to update them independently.
![db](https://gitlab.computing.dcu.ie/pegmana2/2018-ca400-XXXX/raw/master/docs/blog/images/dbGraph.png)

I then implemented the [schema](https://gitlab.computing.dcu.ie/pegmana2/2018-ca400-XXXX/raw/master/src/src/resources/import.sql) in sqlite and 
added it to the project. I made sure to use apropriate datatypes and modifiers such as unique and not null to make sure that any mistakes when inserting data will cause an error asap.


## Create DB access functions

I have written up the sql queries I will need for the project, and added them to [mybatis mappers](https://gitlab.computing.dcu.ie/pegmana2/2018-ca400-XXXX/tree/master/src/src/main/java/space/pegman/redditMod/mappers/) so that I can use them from within the springboot project. I also wrote some [JUnit tests](https://gitlab.computing.dcu.ie/pegmana2/2018-ca400-XXXX/tree/master/src/src/test/java/space/pegman/redditMod/mappers/) so that I could test some of the queries for each table and ensure I had the syntax right.


## Create API Endpoints

I created a springboot http resource for each of the database tables, includeng get, post and delete methods, and created inner classes to hold the requests I would need to recieve. 
I used spring's built in support for Jackson json to serialise and deserialise JSON.

## Connect API to DB

I used spring's autowire feature to get instances of the query mappers in each of the resource classes, and added code to query and write to the database based on requests. Comment sample set collection still needs to be implemented. I also wrote some JUnit tests that both create 


## Create Web UI

I initially tried to use web components for the UI, but decided against it when progress became slow. I instead used plain HTML5 and Sass to create a mockup of the UI.
I changed the structure of the UI to be more straightforward and remove the sidebar, as the UI mockup from the functional spec no longer fit the structure of the underlying code.
Using CSS grid template areas to create the layout was suprisingly straightforward, and I'll definitely be using this feature again in the future. I also used media queries to ensure the 
layout worked on mobile.


## Connect Web UI to API

I used the Javascript XMLHttpRequest prototype implement get, post and delete for each of the endpoints, and JQuery to update the DOM and hook the forms.

## Create reddit auth code

I decided not to use JRAW (an existing reddit client library,) to handle communicating with the Reddit API, as I know from experience that it is slow, hard to debug, and can wait unnecessarily between requests. I also decided against using Spring's OAuthContext to handle the OAuth as Reddit's API documentation recommends using the OAuth2 password flow for single-user scripts, which . I implemented a Spring bean that handles authenticating with the Reddit API and obeying the rate limit.


## Create code to collect comments

I implemented a spring service to get Reddit comments in a thread or subreddit listing. This proved a bit challenging as reddit's api responses are heavily nested and use wrapper classes to give the type of each object in a way that is not supported by any JSON parser. I solved this issue by initially getting the data as an object that contains a Hashmap, then converting the type differently based on the type attribute in the response. The collection is triggered by a http request to the service and runs in a separate thread to avoid blocking and allow multiple collections to happen at once.

![Data structure](https://gitlab.computing.dcu.ie/pegmana2/2018-ca400-XXXX/raw/master/docs/blog/images/RedditResponseStructure.png)

## Create Actions

I created a spring service that consumes the action records from my DB and a reddit comment, and then deletes the reddit comment, or posts a reply, or bans the user based on the action record. 


## Create Triggers

The triggers are responsible for detecting reddit comments that should have an action applied.

### Regex trigger
	
To start I created a basic regex based trigger, which matched comments based on the presence of a regular expression match.

### Baysian, k Nearest and Anomaly detection triggers

I then created the baysian, k-nearest and anomaly detection triggers.

The baysian trigger uses the probability of a given word occurring in a comment in both a good set and a spam set to calculate the combined probability that the comment is spam given the words present.

The K-nearest trigger triggers if 2 of the 3 nearest comments, (by the difference in number of occurrentces of each word) are in the spam set rather than the good set. 

The anomaly detection trigger calculates the probability of each word appearing the number of times it does in the comment, assuming that word frequencies are normally distributed, 
and compares this to a this to a threshold that depends on the number of words in the comment.


### Automatically discovering threshold
Due to time constrainst I couldn't implement a feature to automatically find the best threshold for a trigger by trying several and finding the one that best fit the dataset. 
The triggers would have performed better with this feature.

##Create Subreddit Monitor

I created a spring service that iterates over the list of subreddits to watch from the db and fetches the queue of unseen comments from the subreddit, before applying the rules from the DB.
This service runs in a separate thread from the rest of the application to avoid blocking the webservice. 

## Testing Strategy

I used Junit tests to unit test the database functions, unit test the statistics functions for the triggers and integration test the boundary between the api and the database. 
I also did ad-hoc testing of the triggers, actions and collection by creating [a subreddit for testing](https://www.reddit.com/r/sample_cat_subreddit/) and creating rules of each type.
Whilst doing this I tested that each of the forms on the UI behaved as expected.
