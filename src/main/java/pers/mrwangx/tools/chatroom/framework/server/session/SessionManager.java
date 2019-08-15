package pers.mrwangx.tools.chatroom.framework.server.session;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Set;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月10 16:52
 **/
public interface SessionManager<T extends Session> {

	T add(T session);

	T get(int sessionId);

	T get(SocketChannel channel);

	T remove(T session);

	T remove(SocketChannel channel);

	Set<Integer> getSessionIds();

	Collection<T> getSessions();

}
