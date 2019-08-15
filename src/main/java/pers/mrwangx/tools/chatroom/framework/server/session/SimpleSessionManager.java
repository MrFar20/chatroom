package pers.mrwangx.tools.chatroom.framework.server.session;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月12 10:00
 **/
public class SimpleSessionManager implements SessionManager<Session> {

	private ConcurrentHashMap<Integer, Session> idSessions;
	private ConcurrentHashMap<SocketChannel, Session> channelSessions;

	public SimpleSessionManager() {
		idSessions = new ConcurrentHashMap<>();
		channelSessions = new ConcurrentHashMap<>();
	}

	@Override
	public Session add(Session session) {
		channelSessions.put(session.getChannel(), session);
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
		channelSessions.remove(session.getChannel());
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
