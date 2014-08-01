wschat 0.3
=========

Grails websocket chat Plugin based on existing examples, provides  websocket chat that can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +


Dependency :

	compile ":wschat:0.3" 

This plugin provides  basic chat page, once installed you can access
```
http://localhost:8080/yourapp/wsChat/
```

If you have disabled login as per configuration below, you must set
 
```
session.user
```
with your logged in username.

Then access chat by going to 
```
http://localhost:8080/yourapp/wsChat/chat
```


	 	
# Config.groovy variables required:

Configure properties by adding following to grails-app/conf/Config.groovy under the "wschat" key:

```groovy
/*
* To disable default login page set following
* session.user must be set and now simply
*/
wschat.disable.login = "yes"


/*
* Your page title
*/
wschat.title='Grails Websocket Chat'


/* 
* Your page heading
*/
wschat.heading='Grails Websocket Chat'


/*
* This is the most important configuration 
* in my current version the hostname is being defined by tomcat start up setenv.sh
* In my tomcat setenv.sh I have
* HOSTNAME=$(hostname)
* JAVA_OPTS="$JAVA_OPTS -DSERVERURL=$HOSTNAME"
*
* Now as per below the hostname is getting set to this value
* if not defined wschat will default it localhost:8080
*
*/
wschat.hostname=System.getProperty('SERVERURL')+":8080"

```

#Issues:

Whilst running this plugin on a tomcat server from an application that calls plugin, I have seen:
```
javax.websocket.DeploymentException: Multiple Endpoints may not be deployed to the same path [/WsChatEndpoint]
	at org.apache.tomcat.websocket.server.WsServerContainer.addEndpoint(WsServerContainer.java:209)
	at org.apache.tomcat.websocket.server.WsServerContainer.addEndpoint(WsServerContainer.java:268)
	at javax.websocket.server.ServerContainer$addEndpoint.call(Unknown Source)
```	
This does appear to be a warning and endpoint still works fine

	
# Custom calling plugin
https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Merging-plugin-with-your-own-custom-calls
	
	
# Version info
```

0.3 - 	Private messaging added:  EITHER /pm username,message |  OR /pm username message
		Users list converted from textarea to html div element - easier to set up functions to 
		interact with users logged in. i.e. to create links to pm etc..

0.2	- 	Tidy up of javascript - additional checks enter auto submit - 	
