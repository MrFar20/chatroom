package pers.mrwangx.tools.chatroom.client;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;

import pers.mrwangx.tools.chatroom.framework.client.ChatClient;
import pers.mrwangx.tools.chatroom.framework.protocol.Message;

import static pers.mrwangx.tools.chatroom.util.StringUtil.str;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月15 9:29
 **/
public class SimpleChatClient extends ChatClient<Message> {

	@Override
	public Message parseToMessage(byte[] data) {
		Message msg = null;
		try {
			String strMsg = new String(data, "UTF-8");
			msg = JSON.parseObject(strMsg).toJavaObject(Message.class);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return msg;
	}

	@Override
	public byte[] parseToByteData(Message msg) {
		return JSON.toJSONString(msg).getBytes();
	}

	@Override
	public void onReceiveMessage(Message msg) {
		System.out.println(formatMsg(msg));
	}

	public Message messageFromInput(String input) {
		if (!input.matches("^(cmd|name)::.+$|^(msg::.+::[0-9]+)$")) {
			return null;
		}
		Message msg = null;
		try {
			String[] ss = input.split("::");
			String type = ss[0];
			switch (type) {
			case "id":
				break;
			case "msg":
				msg = Message.newBuilder()
								.type(Message.MESSAGE)
								.content(ss[1])
								.toId(Integer.parseInt(ss[2]))
								.time(System.currentTimeMillis())
								.build();
				break;
			case "cmd":
				msg = Message.newBuilder()
								.type(Message.CMD)
								.content(ss[1])
								.toId(-1)
								.time(System.currentTimeMillis())
								.build();
				break;
			}
		} catch (Exception e) {
			return null;
		}
		return msg;
	}

	private String formatMsg(Message msg) {
		return str(
						"\n--------------------------\n" +
										"%s@%s\t[%s]\n" +
										"%s\n" +
										"--------------------------\n",
						msg.getName(),
						msg.getFromId() < 0 ? "服务器" : msg.getFromId(),
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(msg.getTime())),
						msg.getContent()
		);
	}
}
