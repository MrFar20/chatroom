package pers.mrwangx.tools.chatroom.handler;

import pers.mrwangx.tools.chatroom.protocol.Message;
import pers.mrwangx.tools.chatroom.server.session.Session;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 14:22
 ***/
public interface Handler {

	void handle(Session session, Message message);

}
