package pers.mrwangx.tools.chatroom.framework.server.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;

import pers.mrwangx.tools.chatroom.framework.protocol.Message;
import pers.mrwangx.tools.chatroom.framework.server.ChatServer;

import static pers.mrwangx.tools.chatroom.framework.util.StringUtil.str;
import static pers.mrwangx.tools.chatroom.framework.server.ChatServerLogger.*;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月07日 14:15
 ***/
public abstract class Session<M extends Message> {
	private int sessionId;
	private String name;
	private Date createTime;
	private SocketChannel channel;
	private SelectionKey sKey;
	private long lastHeartBeatTime;

	public Session(SocketChannel cChannel, SelectionKey sKey, int sessionId) {
		this.channel = cChannel;
		this.sKey = sKey;
		this.sessionId = sessionId;
		try {
			info(str("来自[%s]的连接", cChannel.getRemoteAddress()));
			registe();
		} catch (IOException e) {
			info(str("Session[sessionId=%s]注册失败", sessionId));
		}
	}

	@Override
	public String toString() {
		return "Session[sessionId=" + sessionId + ",name=" + name + ",createTime=" + createTime + "]";
	}

	public abstract byte[] parseToByteData(M msg);


	public void write(M msg) {
		byte[] byteMsg = parseToByteData(msg);
		ByteBuffer buffer = ByteBuffer.allocate(ChatServer.MSG_SIZE);
		buffer.clear();
		buffer.put(byteMsg);
		buffer.flip();
		try {
			while (buffer.hasRemaining()) {
				channel.write(buffer);
			}
		} catch (IOException e) {
			warning(this + "写数据错误", e);
		}
	}

	public abstract M registeBackMsg();

	private void registe() throws IOException {
		channel.configureBlocking(false);
		channel.register(sKey.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(ChatServer.MSG_SIZE));
		M msg = registeBackMsg();
		write(msg);
		lastHeartBeatTime = System.currentTimeMillis();
	}

	public Session setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public SelectionKey getsKey() {
		return sKey;
	}

	public Session setsKey(SelectionKey sKey) {
		this.sKey = sKey;
		return this;
	}

	public long getLastHeartBeatTime() {
		return lastHeartBeatTime;
	}

	public Session setLastHeartBeatTime(long lastHeartBeatTime) {
		this.lastHeartBeatTime = lastHeartBeatTime;
		return this;
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
