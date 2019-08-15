package pers.mrwangx.tools.chatroom.framework.server.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;

import pers.mrwangx.tools.chatroom.framework.server.ChatServer;
import pers.mrwangx.tools.chatroom.framework.protocol.Message;

import static pers.mrwangx.tools.chatroom.util.StringUtil.str;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月07日 14:15
 ***/
public class Session {

	public static final Logger log = Logger.getLogger("Session");

	private int sessionId;
	private String name;
	private Date createTime;
	private SocketChannel channel;
	private SelectionKey sKey;

	public Session(SocketChannel cChannel, SelectionKey sKey, int sessionId) {
		this.channel = cChannel;
		this.sKey = sKey;
		this.sessionId = sessionId;
		try {
			log.info(str("来自[%s]的连接", cChannel.getRemoteAddress()));
			registe();
		} catch (IOException e) {
			log.info(e.toString());
		}
	}

	public byte[] parseMessage(Message msg) {
		return JSON.toJSONString(msg).getBytes();
	}

	public void write(Message msg) {
		byte[] byteMsg = parseMessage(msg);
		ByteBuffer buffer = ByteBuffer.allocate(ChatServer.MSG_SIZE);
		buffer.clear();
		buffer.put(byteMsg);
		buffer.flip();
		try {
			while (buffer.hasRemaining()) {
				channel.write(buffer);
			}
		} catch (IOException e) {
			log.info(e.toString());
		}
	}

	public Message registeBackMsg() {
		return Message.newBuilder()
						.fromId(-1)
						.toId(-1)
						.type(Message.ALLOCATE_ID)
						.time(System.currentTimeMillis())
						.content(sessionId + "")
						.build();
	}

	private void registe() throws IOException {
		channel.configureBlocking(false);
		channel.register(sKey.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
		Message msg = registeBackMsg();
		write(msg);
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long time) {
		this.createTime = new Date(time);
	}
}
