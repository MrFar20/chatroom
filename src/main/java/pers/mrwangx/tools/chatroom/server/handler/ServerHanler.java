package pers.mrwangx.tools.chatroom.server.handler;

import java.util.logging.Logger;

import javax.annotation.Resource;

import pers.mrwangx.tools.chatroom.framework.protocol.Message;
import pers.mrwangx.tools.chatroom.framework.server.handler.Handler;
import pers.mrwangx.tools.chatroom.framework.server.session.Session;
import pers.mrwangx.tools.chatroom.framework.server.session.SessionManager;

import static pers.mrwangx.tools.chatroom.framework.protocol.Message.*;
import static pers.mrwangx.tools.chatroom.util.Tools.str;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 14:25
 ***/
public class ServerHanler implements Handler {

	@Resource
	Logger log;
	@Resource
	private SessionManager<Session> sessionManager;

	@Override
	public void handle(Session session, Message message) {
		int msgType = message.getType();
		log.info(str("处理请求:%s", message));
		switch (msgType) {
		case ALLOCATE_ID:
			break;
		case UPDATE_NAME:
			session.setName(message.getContent());
			break;
		case MESSAGE:
			Session sessionTo = sessionManager.get(message.getToId());
			message.setFromId(session.getSessionId());
			message.setName(session.getName());
			sessionTo.write(message);
			break;
		case CMD:
			StringBuffer buffer = new StringBuffer();
			sessionManager.getSessions().forEach(s -> {
				buffer.append(str("[ID:%d, 名字:%s]\n", s.getSessionId(), s.getName()));
			});
			Message msg1 = Message.newBuilder()
							.type(MESSAGE)
							.fromId(-1)
							.name("server")
							.toId(session.getSessionId())
							.time(System.currentTimeMillis())
							.content(buffer.toString())
							.build();
			session.write(msg1);
			break;
		}
	}
}


