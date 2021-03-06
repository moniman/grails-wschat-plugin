wschat
=========

Grails websocket chat Plugin provides a multi-chat room facilty to an existing grails based site/application.


##### grails wschat plugin supports:
```
User roles (Admin/regular user)
Admin can:  kick/Ban (for specified time period)
Users can create profiles define details and upload photos.
Chat rooms can be created in Config.groovy +/ DB once logged in using UI.
0.19+ supports webcam tested  on chrome/firefox.  
1.0+ supports WebRTC (HD: Video/Audio streaming using cam/mic) currently only on Chrome Canary.
```

 Websocket chat can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+


###### Dependency :
```groovy
	compile ":wschat:1.11-SNAPSHOT" 
```

This plugin provides  basic chat page, once installed you can access
```
http://localhost:8080/yourapp/wsChat/
```
		

##### Custom calling plugin disabled login
[Custom calling plugin](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Custom-calling-plugin-disabled-login)

##### Custom calls 
[Custom calls](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Custom-calls)

 
##### Videos:
[Video: grails app running wschat 0.14 part1](https://www.youtube.com/watch?v=E-NmbDZg9G4)

[Video: grails app running wschat 0.14 part2](https://www.youtube.com/watch?v=xPxV_iEYYm0)

[Video: WsChatClintEndPoint Client/Server Messaging via chat client API](https://www.youtube.com/watch?v=zAySkzNid3E)


##### WebtRTC WebCam walk through
[WebtRTC WebCam walk through](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebtRTC-WebCam-walk-through)


##### Config.groovy variables required:
 [Config.groovy variables required:](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Config.groovy)

#### STUN Server, setting up your own server:
[WebRTC-terminology](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/WebRTC-terminology)


##### Creating admin accounts
[Creating admin accounts](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Creating-admin-accounts)
	

##### 0.10+ & resources based apps (pre 2.4)
[pre 2.4 apps](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/resources-based-apps)

##### ChatClientEndPoint Client/Server Messaging  new feature since 1.11
[wsChatClientEndPoint explained](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/wsChatClientEndPoint-new-feature-since-1.11)

 
#####Commands
[Commands](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Commands)

#####Version info
[Version info](https://github.com/vahidhedayati/grails-wschat-plugin/wiki/Version-info)


### Known issues/work arounds:
Since 0.20+ ui.videobox has been added, earlier versions and even current version suffers from conflicts with jquery.ui.chatbox and does not send message. Currently the temporary fix is when you open a cam/rtc session to another user or as the initiator. If you did have pm boxes open you will find they will all close down as you open the video box. If you then go to the user and click on PM your currently pm history will reappear along with their pm box.

