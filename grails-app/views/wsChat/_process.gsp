
process page
<g:javascript>
	var loggedInUsers=[];
	var user="${user }";
	var receivers="${receivers}"
	var arrayLength = receivers.length;
	// Connect websocket and set up processes 
	var webSocket=new WebSocket("ws://${hostname}/${appName}/${chatApp }/${room}");
 	webSocket.onopen=function(message) {processOpen(message);};
 	webSocket.onclose=function(message) {processClose(message);};
    webSocket.onerror=function(message) {processError(message);};
	webSocket.onmessage=function(message) {processMessage(message);	};
	
	// Global variables
	var userList=[];
	
	function processMessage( message) {
	
		//Create internal users list
		// and log out front end user when backend - real user has logged out
		var jsonData = JSON.parse(message.data);
		if (jsonData.users!=null) {
			loggedInUsers=[];
			updateUserList(jsonData.users);
			var backendLoggedin=verifyIsOn(user);
			if (backendLoggedin=="false") {
				//console.log('Backend user of mine has left so I need to be logged out now');
				webSocket.send("DISCO:-");
       	 		webSocket.close();
			}
			//console.log(loggedInUsers);	
		}
	
		// Log out user if system tells it to	
		if (jsonData.system!=null) {
			if (jsonData.system=="disconnect") { 
				webSocket.send("DISCO:-"+user);
				webSocket.close();
			}
		}
		
		
		//Process private Messages:
		if (jsonData.privateMessage!=null) {
			var receiver
			var sender
			if (jsonData.msgFrom!=null) {
				sender=jsonData.msgFrom
			}
			if (jsonData.msgTo!=null) {
				receiver=jsonData.msgTo
			}
			
			console.log("--->"+jsonData.privateMessage);
		//$('#chatMessages').append("PM("+sender+"): "+jsonData.privateMessage+"\n");
		//sendPM(receiver,sender,jsonData.privateMessage);
		}   	
		
	}

	
	function updateUserList(users) {
		if (users!=null) {
			users.forEach(function(entry) {
				if (entry.owner_rtc!=null) {
					addUser(entry.owner_rtc);
				}
				if (entry.owner_av!=null) {
					addUser(entry.owner_av);
				}
				if (entry.owner!=null) {
					addUser(entry.owner);
				}			
				if (entry.friends_rtc!=null) {
					addUser(entry.friends_rtc);
				}	
				if (entry.friends_av!=null) {
					addUser(entry.friends_av);
				}
				if (entry.friends!=null) {
					addUser(entry.friends);
				}
				if (entry.user_rtc!=null) {
					addUser(entry.user_rtc);
				}
				if (entry.user_av!=null) {
					addUser(entry.user_av);
				}
				if (entry.user!=null) {
					addUser(entry.user);
				}
				if (entry.blocked!=null) {
				}
			});
			
		}	
	}

	function processError(message) {
		console.log(message);
	}

	function verifyIsOn(uid) {
		var ison="false";
		var idx = loggedInUsers.indexOf(uid);
		if (idx != -1) {
			ison="true";
		}
		return ison;
	}	
	
	function addUser(uid) {
		var idx = loggedInUsers.indexOf(uid);
		if(idx == -1) {
			loggedInUsers.push(uid);
		}	
	}
	
	function processClose(message) {
		webSocket.send("DISCO:-"+user);
		$('#chatMessages').append(user+" disconnecting from server... \n");
		webSocket.close();
	}


	// Open connection only if we have frontuser variable    
 	function processOpen(message) {
    	<g:if test="${frontuser}">
    		webSocket.send("CONN:-${frontuser}");
       	</g:if>
       	<g:else>
       		$('#chatMessages').append("Chat denied no username \n");
       		webSocket.send("DISCO:-");
       	 	webSocket.close();
       </g:else>
 	}
 	


	function processCommands(jsonCommands) {
	    if(jsonCommands.command !== undefined){
	        switch(jsonCommands.command){
	            case "javascript":
	                executeJavascript(jsonCommands.arguments);
	                break;
	            case "closeTab":
	                var args = getParametersFromMessage(jsonCommands.arguments);
	                var oFunction = tabRemoveActiveInMain || window.parent.tabRemoveActiveInMain;
	                oFunction(args[0], args[1]);
	                break;
				 default:
				 	     break;             
			};
	    }
    }

     window.onbeforeunload = function() {
    	 webSocket.send("DISCO:-"+user);
       	//webSocket.onclose = function() { }
       	//webSocket.close();
     }
</g:javascript>