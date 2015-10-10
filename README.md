# TwitterC
Twitter REST client in JavaFX !

**Table of Contents**

- [Description](#)
- [Some views](#)
	- [Profile view](#)
	- [Search view](#)
- [Usage](#)
- [Build](#)
- [Modification](#)

## Description
This java application provides basic support of Twitter, using the Twitter REST api (through Twitter4j). Among all the functionality, you'll be able to :

- See your profile (tweets, favorites, etc..)
- See anybody profile
- Send tweet
 
The general style of the application is pretty much a visual rip-off of twitter's web interface, using JavaFx. But hey, that's a cool style.
 
## Some views
### Profile view
![alt tag](http://i.imgur.com/zO2kxrd.png)

### Search view
![alt tag](http://i.imgur.com/HlsZeBF.png)

## Usage
Just run the provided Jar !

## Build
Note : you'll need an app public/private key from twitter to run this, and replace it in the properties of the pom.xml file :

    <properties>
        <api-key>your-key</api-key>
        <api-secret>your-secret</api-secret>
    </properties>

This a maven project, so all you need to do is what you usually do to build maven project :

   mvn package

And you'll have a runnable jar containing the application and all its dependencies.

## Modification
This project is done with Intellij, so all the related project's files are still here. And yes, the project was commited with API key, but don't worry I've changed them !
