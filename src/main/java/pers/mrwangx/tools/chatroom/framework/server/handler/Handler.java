package pers.mrwangx.tools.chatroom.framework.server.handler;

import pers.mrwangx.tools.chatroom.framework.protocol.Message;
import pers.mrwangx.tools.chatroom.framework.server.session.Session;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 14:22
 ***/
public interface Handler<S extends  Session<M>, M extends Message> {

	void handle(S session, M message);

}
