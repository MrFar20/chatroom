package pers.mrwangx.tools.chatroom;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pers.mrwangx.tools.chatroom.server.ChatServer;
import pers.mrwangx.tools.chatroom.server.conf.SpringConfiguration;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 15:20
 ***/
public class App {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		ChatServer server = context.getBean("chatServer", ChatServer.class);
		server.run();
	}

}
