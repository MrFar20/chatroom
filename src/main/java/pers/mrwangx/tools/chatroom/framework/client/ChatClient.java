package pers.mrwangx.tools.chatroom.framework.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import pers.mrwangx.tools.chatroom.framework.protocol.Message;
import static pers.mrwangx.tools.chatroom.util.Tools.*;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月12 15:00
 **/
public abstract class ChatClient <T extends Message> {

	public static final Logger log = Logger.getLogger("ChatClient");
	private static int MSG_SIZE = 2048;

	protected InetSocketAddress address;
	protected SocketChannel channel;
	protected ReceiveTask receiveTask;
	protected ByteBuffer buffer;

	public ChatClient() {
		buffer = ByteBuffer.allocate(MSG_SIZE);
	}


	public boolean connect(InetSocketAddress address) {
		boolean flag = false;
		try {
			log.info("初始化channel...");
			this.channel = SocketChannel.open();
			log.info(str("连接服务器[%s:%d]中...", address.getHostName(), address.getPort()));
			channel.connect(address);
			if (channel.finishConnect() && channel.isConnected()) {
				log.info("连接成功");
				(this.receiveTask = new ReceiveTask()).start();
				flag = true;
				this.address = address;
			}
		} catch (IOException e) {
			log.warning("连接失败:" + e.toString());
		}
		return flag;
	}

	public boolean connect(String host, int port) {
		return connect(new InetSocketAddress(host, port));
	}

	public abstract T parseToMessage(byte[] data);

	public abstract byte[] parseToByteData(T msg);

	public abstract void onReceiveMessage(T msg);

	public void sendMessage(T msg) {
		byte[] data = parseToByteData(msg);
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		while (buffer.hasRemaining()) {
			try {
				channel.write(buffer);
			} catch (IOException e) {
				log.warning("发送数据出错:" + e.toString());
			}
		}
		buffer.clear();
	}

	private class ReceiveTask extends Thread {

		@Override
		public void run() {
			while (true) {
				ByteBuffer bufferRec = ByteBuffer.allocate(MSG_SIZE);
				byte[] data = new byte[MSG_SIZE];
				int i = 0;
				try {
					while (channel.read(bufferRec) > 0) {
						bufferRec.flip();
						while (bufferRec.hasRemaining()) {
							data[i++] = bufferRec.get();
						}
					}
					if (i > 0) {
						T msg = parseToMessage(data);
						onReceiveMessage(msg);
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}



}