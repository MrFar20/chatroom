package pers.mrwangx.tools.chatroom.handler;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import pers.mrwangx.tools.chatroom.protocol.Message;
import pers.mrwangx.tools.chatroom.server.session.Session;
import pers.mrwangx.tools.chatroom.server.session.impl.SessionManagerImpl;

import static pers.mrwangx.tools.chatroom.protocol.Message.*;
import static pers.mrwangx.tools.chatroom.util.Tools.str;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 14:25
 ***/
@Component
public class ServerHanler implements Handler {

	@Resource
	Logger log;
	@Resource
	private SessionManagerImpl sessionManager;
	@Resource
	private ExecutorService executorPool;

	@Override
	public void handle(Session session, Message message) {
		HandleTask task = new HandleTask(session, message);
		executorPool.execute(task);
	}

	private class HandleTask implements Runnable {

		Session session;
		Message message;

		public HandleTask(Session session, Message message) {
			this.session = session;
			this.message = message;
		}

		@Override
		public void run() {
			int msgType = message.getType();
			log.info(str("处理请求:%s", message));
			switch (msgType) {
			case ID:
				break;
			case UPDATE_NAME:
				session.setName(message.getContent());
				break;
			case MESSAGE:
				Session sessionTo = sessionManager.get(message.getToId());
				message.setFromId(session.getSessionId());
				message.setName(sessionTo.getName());
				sessionTo.write(message);
				break;
			case CMD:
				StringBuffer buffer = new StringBuffer();
				sessionManager.getSessions().forEach(session -> {
					buffer.append(str("[ID:%d, 名字:%s]\n", session.getSessionId(), session.getName()));
				});
				Message msg1 = new Message();
				msg1.setType(MESSAGE);
				msg1.setFromId(-1);
				msg1.setName("服务器");
				msg1.setToId(session.getSessionId());
				msg1.setTime(System.currentTimeMillis());
				msg1.setContent(buffer.toString());
				session.write(msg1);
				break;
			}
		}


	}

}
