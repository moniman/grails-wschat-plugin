package grails.plugin.wschat.users

import grails.converters.JSON
import grails.plugin.wschat.ChatBanList
import grails.plugin.wschat.ChatBlockList
import grails.plugin.wschat.ChatFriendList
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.interfaces.ChatSessions
import grails.transaction.Transactional
import groovy.time.TimeCategory

import java.text.SimpleDateFormat

import javax.websocket.Session

//@Transactional
class WsChatUserService extends WsChatConfService  implements ChatSessions {

	def wsChatMessagingService

	void kickUser(Session userSession,String username) {
		Boolean useris = isAdmin(userSession)
		if (useris) {
			logoutUser(userSession,username)
		}
	}

	def logUserOut(Session userSession,String username,String room) {
		userSession.close()
		removeUser(username)
		String bcasto = config.left.timeout  ?: '0'
		sleep(bcasto as int)
		sendUsersLoggedOut(room,username)
	}

	private void banUser(Session userSession,String username,String duration,String period) {
		Boolean useris = isAdmin(userSession)
		if (useris) {
			banthisUser(username,duration,period)
			logoutUser(userSession,username)
		}
	}
	
	private void logoutUser(String username) {
		def myMsg = [:]
		myMsg.put("message", "${username} about to be kicked off")
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def uList = []
						def finalList = [:]
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							def myMsg1 = [:]
							myMsg1.put("system","disconnect")
							wsChatMessagingService.messageUser(crec,myMsg1)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}
	private void logoutUser(Session userSession,String username) {
		def myMsg = [:]
		myMsg.put("message", "${username} about to be kicked off")
		wsChatMessagingService.broadcast(userSession,myMsg)
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def uList = []
						def finalList = [:]
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							def myMsg1 = [:]
							myMsg1.put("system","disconnect")
							wsChatMessagingService.messageUser(crec,myMsg1)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	boolean findUser(String username) {
		boolean found = false
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							found = true
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		return found
	}
	
	private ArrayList genUserMenu(ArrayList friendslist, ArrayList blocklist, String room, String uiterator ) {
		def uList = []
		try {

			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					def myUser = [:]
					if (crec && crec.isOpen() && room.equals(crec.userProperties.get("room"))) {
						def cuser = crec.userProperties.get("username").toString()
						def av = crec.userProperties.get("av").toString()
						def rtc = crec.userProperties.get("rtc").toString()
						def addav = ""
						if (av.equals("on")) {
							addav = "_av"
						}
						if (rtc.equals("on")) {
							addav = "_rtc"
						}
						if (cuser.equals(uiterator)) {
							myUser.put("owner${addav}", cuser)
							uList.add(myUser)
						}else{
							if ((blocklist)&&(blocklist.username.contains(cuser))) {
								myUser.put("blocked", cuser)
								uList.add(myUser)

							}else if  ((friendslist)&&(friendslist.username.contains(cuser))) {
								myUser.put("friends${addav}", cuser)
								uList.add(myUser)
							}else{
								myUser.put("user${addav}", cuser)
								uList.add(myUser)
							}
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		return uList
	}

	void sendUsers(Session userSession,String username) {
		String room  =  userSession.userProperties.get("room") as String
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec2->
					if (crec2 && crec2.isOpen()) {
						String uiterator = crec2.userProperties.get("username").toString()

						def finalList = [:]
						def blocklist
						def friendslist
						if (dbSupport()) {
							ChatBlockList.withTransaction {
								blocklist = ChatBlockList.findAllByChatuser(currentUser(uiterator))
							}
							ChatFriendList.withTransaction {
								friendslist = ChatFriendList.findAllByChatuser(currentUser(uiterator))
							}
						}

						def uList = genUserMenu(friendslist, blocklist, room, uiterator)
						finalList.put("users", uList)
						sendUserList(uiterator,finalList)
					}
				}
			}

		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}

	}

	void sendUsersLoggedOut(String room,String username) {

		try {
			def myMsg = [:]
			String sendleave = config.send.leftroom  ?: 'yes'
			myMsg.put("message", "${username} has left ${room}")

			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec2->
					if (crec2 && crec2.isOpen()) {
						def uiterator = crec2.userProperties.get("username").toString()
						if (uiterator != username) {
							//broadcast(crec2,myMsg)
							//isuBanned = isBanned(username)
							//if (!isuBanned && (sendleave= = 'yes')){
							if (sendleave == 'yes') {
								wsChatMessagingService.broadcast(crec2,myMsg)
							}

							def finalList = [:]
							def blocklist
							def friendslist
							if (dbSupport()) {
								ChatBlockList.withTransaction {
									blocklist = ChatBlockList.findAllByChatuser(currentUser(uiterator))
								}
								ChatFriendList.withTransaction {
									friendslist = ChatFriendList.findAllByChatuser(currentUser(uiterator))
								}
							}
							def uList = genUserMenu(friendslist, blocklist, room, uiterator)
							finalList.put("users", uList)
							sendUserList(uiterator,finalList)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}

	}

	private void sendUserList(String iuser,Map msg) {
		def myMsgj = msg as JSON
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser=='null') {
							removeUser(cuser)
							//logoutUser(cuser)
						}
						if (cuser.equals(iuser)) {
							crec.basicRemote.sendText(myMsgj as String)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	private void removeUser(String username) {
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							iterator.remove()
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	private void unblockUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser = currentUser(username)
			ChatBlockList.withTransaction {
				def found = ChatBlockList.findByChatuserAndUsername(cuser,urecord)
				found.delete(flush: true)
			}
		}
	}

	private void banthisUser(String username,String duration, String period) {
		def cc
		use(TimeCategory) {
			cc = new Date() +(duration as int)."${period}"
		}
		def current  =  new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cc)
		ChatBanList.withTransaction {
			def found = ChatBanList.findByUsername(username)
			if (!found) {
				def newEntry = new ChatBanList()
				newEntry.username = username
				newEntry.period = current
				newEntry.save(flush:true)
			}else{
				found.period = current
				found.save(flush:true)
			}
		}
	}

	private void blockUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser = currentUser(username)
			ChatBlockList.withTransaction {
				def found = ChatBlockList.findByChatuserAndUsername(cuser,urecord)
				if (!found) {
					def newEntry = new ChatBlockList()
					newEntry.chatuser = cuser
					newEntry.username = urecord
					newEntry.save(flush:true)
				}
			}
		}
	}

	private void addUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser = currentUser(username)
			ChatFriendList.withTransaction {
				def found = ChatFriendList.findByChatuserAndUsername(cuser,urecord)
				if (!found) {
					def newEntry = new ChatFriendList()
					newEntry.chatuser = cuser
					newEntry.username = urecord
					newEntry.save(flush:true)
				}
			}
		}
	}

	private void removeUser(String username,String urecord) {
		if (dbSupport()) {
			ChatFriendList.withTransaction {
				def cuser = currentUser(username)
				def found = ChatFriendList.findByChatuserAndUsername(cuser,urecord)
				found.delete(flush: true)
			}
		}
	}


	public Boolean loggedIn(String user) {
		Boolean loggedin = false
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							loggedin = true
						}
					}
				}
			}
		} catch (IOException e) {
			log.info ("onMessage failed", e)
		}
		return loggedin
	}

	private String getCurrentUserName(Session userSession) {
		def myMsg = [:]
		String username = userSession.userProperties.get("username") as String
		if (!username) {
			myMsg.put("message","Access denied no username defined")
			wsChatMessagingService.messageUser(userSession,myMsg)
			//chatroomUsers.remove(userSession)
		}else{
			return username
		}
	}
}
