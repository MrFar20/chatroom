package pers.mrwangx.tools.chatroom.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;

import pers.mrwangx.tools.chatroom.protocol.Message;

import static pers.mrwangx.tools.chatroom.util.Tools.str;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 11:21
 ***/
public class ChatClient {

	public static void main(String[] args) {
		new ChatClient("localhost", 8066).run();
		// Scanner input = new Scanner(System.in);
		// while (true) {
		// 	System.out.println(input.nextLine().matches("^(cmd|name)::.+$|^(msg::.+::[0-9]+)$"));
		// }
	}

	private static final Logger log = Logger.getLogger("ChatClient");
	private Scanner input = new Scanner(System.in);
	private final int MAX_MSG_LEN = 2048;

	private String HOSTNAME;
	private int HOSTPORT;
	private SocketChannel channel;
	private int sessionid;

	public ChatClient(String HOSTNAME, int HOSTPORT) {
		this.HOSTNAME = HOSTNAME;
		this.HOSTPORT = HOSTPORT;
	}

	public void run() {
		initClient();
		try {
			if (channel != null && channel.finishConnect()) {
				log.info(str("连接服务器[%s:%d]成功", HOSTNAME, HOSTPORT));
				new ReceiveTask().start();
				ByteBuffer buffer = ByteBuffer.allocate(MAX_MSG_LEN);
				System.out.print("请输入您聊天的名字:");
				String name = input.nextLine();
				sendMsg(createMsg(Message.UPDATE_NAME, name, -1, System.currentTimeMillis()), buffer, channel);
				while (true) {
					String content = input.nextLine();
					if (content.equals("#")) {
						break;
					}
					Message msg = getMessageFromInput(content);
					if (msg == null) {
						System.out.println("请输入正确格式");
					} else {
						sendMsg(msg, buffer, channel);
					}
				}
			}
		} catch (IOException e) {
			log.info("客户端运行出错");
		}
	}

	private Message getMessageFromInput(String input) {
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
				msg = createMsg(Message.MESSAGE, ss[1], Integer.parseInt(ss[2]), System.currentTimeMillis());
				break;
			case "cmd":
				msg = createMsg(Message.CMD, ss[1], -1, System.currentTimeMillis());
				break;
			}
		} catch (Exception e) {
			return null;
		}
		return msg;
	}

	private void sendMsg(Message msg, ByteBuffer buffer, SocketChannel channel) throws IOException {
		String strMsg = JSON.toJSONString(msg);
		buffer.clear();
		buffer.put(strMsg.getBytes());
		buffer.flip();
		while (buffer.hasRemaining()) {
			channel.write(buffer);
		}
		buffer.clear();
	}

	private Message createMsg(int type, String content, int toId, long time) {
		Message msg = new Message();
		msg.setType(type);
		msg.setContent(content);
		msg.setToId(toId);
		msg.setTime(time);
		return msg;
	}

	private void initClient() {
		try {
			channel = SocketChannel.open();
			log.info(str("连接服务器[%s:%d]...", HOSTNAME, HOSTPORT));
			channel.connect(new InetSocketAddress(HOSTNAME, HOSTPORT));
			channel.configureBlocking(false);
		} catch (IOException e) {
			log.info("客户端初始化失败");
		}
	}

	private class ReceiveTask extends Thread {

		@Override
		public void run() {
			while (true) {
				ByteBuffer buffer1 = ByteBuffer.allocate(1024);
				byte[] data = new byte[1024];
				int i = 0;
				try {
					while (channel.read(buffer1) > 0) {
						buffer1.flip();
						while (buffer1.hasRemaining()) {
							data[i++] = buffer1.get();
						}
					}
					if (i > 0) {
						String msg = new String(data, 0, i, "UTF-8");
						System.out.println(formatMsg(JSON.parseObject(msg).toJavaObject(Message.class)));
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
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
