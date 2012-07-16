package com.unito.tableplus.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public final class ServiceFactory {
	private static PersistenceManagerFactory pmfInstance;
	private static MemcacheService syncCache;
	private static UserService userService;
	private static ChannelService channelService;
	
	public static PersistenceManagerFactory getPmfInstance() {
		if (pmfInstance == null)
			pmfInstance = JDOHelper
					.getPersistenceManagerFactory("transactions-optional");
		return pmfInstance;
	}

	public static MemcacheService getSyncCache() {
		if (syncCache == null)
			syncCache = MemcacheServiceFactory.getMemcacheService();
		return syncCache;
	}

	public static UserService getUserService() {
		if (userService == null)
			userService = UserServiceFactory.getUserService();
		return userService;

	}
	
	public static ChannelService getChannelService(){
		if(channelService ==  null)
			channelService = ChannelServiceFactory.getChannelService();
		return channelService;
	}
}