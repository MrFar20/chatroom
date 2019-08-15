package pers.mrwangx.tools.chatroom.framework.server.handler;

import pers.mrwangx.tools.chatroom.framework.protocol.Message;
import pers.mrwangx.tools.chatroom.framework.server.session.Session;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 14:22
 ***/
public interface Handler {

	void handle(Session session, Message message);

}