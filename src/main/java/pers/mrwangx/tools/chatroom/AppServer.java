package pers.mrwangx.tools.chatroom;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pers.mrwangx.tools.chatroom.server.SimpleChatServer;
import pers.mrwangx.tools.chatroom.server.conf.SpringConfiguration;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 15:20
 ***/
public class AppServer {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		SimpleChatServer server = context.getBean("chatServer", SimpleChatServer.class);
		server.start();
	}

}
