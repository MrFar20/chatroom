package pers.mrwangx.tools.chatroom.server.session.impl;

import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import pers.mrwangx.tools.chatroom.server.session.Session;
import pers.mrwangx.tools.chatroom.server.session.SessionManager;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 13:27
 ***/
@Component
public class SessionManagerImpl implements SessionManager {

	private ConcurrentHashMap<Integer, Session> idSessions;
	private ConcurrentHashMap<SocketChannel, Session> channelSessions;

	public SessionManagerImpl() {
		idSessions = new ConcurrentHashMap<>();
		channelSessions = new ConcurrentHashMap<>();
	}

	@Override
	public Session add(Session session) {
		channelSessions.put(session.getcChannel(), session);
		return idSessions.put(session.getSessionId(), session);
	}

	@Override
	public Session get(int sessionId) {
		return idSessions.get(sessionId);
	}

	@Override
	public Session get(SocketChannel channel) {
		return channelSessions.get(channel);
	}

	@Override
	public Session remove(Session session) {
		channelSessions.remove(session.getcChannel());
		return idSessions.remove(session.getSessionId());
	}

	@Override
	public Session remove(SocketChannel channel) {
		Session session = channelSessions.remove(channel);
		return idSessions.remove(session.getSessionId());
	}

	@Override
	public Set<Integer> getSessionIds() {
		return idSessions.keySet();
	}

	@Override
	public Collection<Session> getSessions() {
		return idSessions.values();
	}

}
