package pers.mrwangx.tools.chatroom.server.session;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Set;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月10 16:52
 **/
public interface SessionManager {

	Session add(Session session);

	Session get(int sessionId);

	Session get(SocketChannel channel);

	Session remove(Session session);

	Session remove(SocketChannel channel);

	Set<Integer> getSessionIds();

	Collection<Session> getSessions();

}
