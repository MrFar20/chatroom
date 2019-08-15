package pers.mrwangx.tools.chatroom;

import java.util.Scanner;

import pers.mrwangx.tools.chatroom.client.SimpleChatClient;
import pers.mrwangx.tools.chatroom.framework.protocol.Message;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月15 9:56
 **/
public class AppClient {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		SimpleChatClient client = new SimpleChatClient();
		boolean success = client.connect(args[0], Integer.parseInt(args[1]));
		if (success) {
			System.out.print("请输入您聊天的名字:");
			String name = input.nextLine();
			client.sendMessage(Message
							.newBuilder()
							.type(Message.UPDATE_NAME)
							.content(name)
							.toId(-1)
							.time(System.currentTimeMillis())
							.build()
			);
			while (true) {
				String content = input.nextLine();
				if (content.equals("#")) {
					break;
				}
				Message msg = client.messageFromInput(content);
				if (msg == null) {
					System.out.println("请输入正确格式");
				} else {
					client.sendMessage(msg);
				}
			}
		}

	}

}
