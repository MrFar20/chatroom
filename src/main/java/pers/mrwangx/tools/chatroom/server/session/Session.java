package pers.mrwangx.tools.chatroom.server.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;

import pers.mrwangx.tools.chatroom.protocol.Message;
import pers.mrwangx.tools.chatroom.server.ChatServer;

import static pers.mrwangx.tools.chatroom.util.Tools.str;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月07日 14:15
 ***/
public class Session {

	public static final Logger log = Logger.getLogger("Session");

	private int sessionId;
	private String name;
	private SocketChannel cChannel;
	private SelectionKey sKey;

	public Session(SocketChannel cChannel, SelectionKey sKey, int sessionId) {
		this.cChannel = cChannel;
		this.sKey = sKey;
		this.sessionId = sessionId;
		try {
			log.info(str("来自[%s]的连接", cChannel.getRemoteAddress()));
			register();
		} catch (IOException e) {
			log.info(e.toString());
		}
	}

	public void write(Message msg) {
		String strMsg = JSON.toJSONString(msg);
		ByteBuffer buffer = ByteBuffer.allocate(ChatServer.MSG_SIZE);
		buffer.clear();
		buffer.put((strMsg).getBytes());
		buffer.flip();
		try {
			while (buffer.hasRemaining()) {
				cChannel.write(buffer);
			}
		} catch (IOException e) {
			log.info(e.toString());
		}
	}

	private void register() throws IOException {
		cChannel.configureBlocking(false);
		cChannel.register(sKey.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
		write(idMsg());
	}

	private Message idMsg() {
		Message msg = new Message();
		msg.setContent("你的id是:" + sessionId);
		msg.setTime(System.currentTimeMillis());
		msg.setType(Message.ID);
		msg.setFromId(-1);
		msg.setToId(-1);
		return msg;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public SocketChannel getcChannel() {
		return cChannel;
	}

	public void setcChannel(SocketChannel cChannel) {
		this.cChannel = cChannel;
	}

	public SelectionKey getsKey() {
		return sKey;
	}

	public void setsKey(SelectionKey sKey) {
		this.sKey = sKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
