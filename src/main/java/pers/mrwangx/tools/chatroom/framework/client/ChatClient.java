package pers.mrwangx.tools.chatroom.framework.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import pers.mrwangx.tools.chatroom.framework.protocol.Message;

import static pers.mrwangx.tools.chatroom.framework.client.ChatClientLogger.info;
import static pers.mrwangx.tools.chatroom.framework.client.ChatClientLogger.warning;
import static pers.mrwangx.tools.chatroom.framework.util.StringUtil.str;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月12 15:00
 **/
public abstract class ChatClient <M extends Message> {

	private static int MSG_SIZE = 2048;
	private long heartBeatInterval;

	protected InetSocketAddress address;
	protected SocketChannel channel;
	protected ReceiveTask receiveTask; //接收信息的线程
	protected HeartBeatTask heartBeatTask;
	protected ByteBuffer buffer;
	protected boolean stoped = true;

	public ChatClient(long heartBeatInterval) {
		this(heartBeatInterval, MSG_SIZE);
	}

	public ChatClient(long heartBeatInterval, int MSG_SIZE) {
		this.heartBeatInterval = heartBeatInterval;
		this.MSG_SIZE = MSG_SIZE;
		buffer = ByteBuffer.allocate(MSG_SIZE);
	}

	public boolean connect(InetSocketAddress address) {
		boolean flag = false;
		try {
			info("初始化channel...");
			this.channel = SocketChannel.open();
			info(str("连接服务器[%s:%d]中...", address.getHostName(), address.getPort()));
			channel.connect(address);
			if (channel.finishConnect() && channel.isConnected()) {
				info("连接成功");
				stoped = false;
				(this.receiveTask = new ReceiveTask()).start();
				(this.heartBeatTask = new HeartBeatTask()).start();
				flag = true;
				this.address = address;
			}
		} catch (IOException e) {
			warning("连接失败:", e);
		}
		return flag;
	}

	public boolean connect(String host, int port) {
		return connect(new InetSocketAddress(host, port));
	}

	public boolean reConnect() {
		return connect(this.address);
	}

	public boolean stop() {
		boolean flag = true;
		stoped = true;
		try {
			channel.close();
		} catch (IOException e) {
			warning("关闭客户端错误", e);
			flag = false;
		}
		return flag;
	}

	/**
	 * 将接收的字节数据转换为自定义的Message对象
	 *
	 * @param data
	 * @return
	 */
	public abstract M parseToMessage(byte[] data);

	/**
	 * 将Message对象转换为字节数据
	 *
	 * @param msg
	 * @return
	 */
	public abstract byte[] parseToByteData(M msg);

	/**
	 * 接收到信息，进行处理
	 *
	 * @param msg
	 */
	public abstract void onReceiveMessage(M msg);

	/**
	 * 心跳包
	 *
	 * @return
	 */
	public abstract M heartBeatMsg();

	/**
	 * 发送信息
	 *
	 * @param msg
	 */
	public void sendMessage(M msg) {
		byte[] data = parseToByteData(msg);
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		try {
			while (buffer.hasRemaining()) {
				channel.write(buffer);
			}
		} catch (IOException e) {
			warning("发送数据出错", e);
		}
		buffer.clear();
	}

	private class ReceiveTask extends Thread {

		@Override
		public void run() {
			while (!stoped && channel.isConnected()) {
				ByteBuffer bufferRec = ByteBuffer.allocate(MSG_SIZE);
				byte[] data = new byte[MSG_SIZE];
				int i = 0;
				try {
					while (!stoped && channel.read(bufferRec) > 0) {
						bufferRec.flip();
						while (!stoped && bufferRec.hasRemaining()) {
							data[i++] = bufferRec.get();
						}
					}
					if (i > 0) {
						byte[] datar = new byte[i];
						System.arraycopy(data, 0, datar, 0, i);
						M msg = parseToMessage(datar);
						onReceiveMessage(msg);
					}
				} catch (IOException e) {
					warning("读取数据出错", e);
					break;
				}
			}
		}
	}

	private class HeartBeatTask extends Thread {

		@Override
		public void run() {
			M message = heartBeatMsg();
			while (!stoped && channel.isConnected()) {
				sendMessage(message);
				try {
					Thread.sleep(heartBeatInterval);
				} catch (InterruptedException e) {
					warning("发送心跳包错误", e);
				}
			}
		}
	}

	public long getHeartBeatInterval() {
		return heartBeatInterval;
	}

	public ChatClient<M> setHeartBeatInterval(long heartBeatInterval) {
		this.heartBeatInterval = heartBeatInterval;
		return this;
	}

	public boolean isStoped() {
		return stoped;
	}
}
