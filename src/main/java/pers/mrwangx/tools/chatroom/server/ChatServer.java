package pers.mrwangx.tools.chatroom.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import pers.mrwangx.tools.chatroom.handler.Handler;
import pers.mrwangx.tools.chatroom.protocol.Message;
import pers.mrwangx.tools.chatroom.server.session.Session;
import pers.mrwangx.tools.chatroom.server.session.impl.SessionManagerImpl;

import static pers.mrwangx.tools.chatroom.util.Tools.*;


/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月07日 13:44
 ***/

public class ChatServer {

	@Resource
	Logger log;

	private int id = 0;

	@Resource
	private SessionManagerImpl sessionManager;
	private Handler handler;

	private int PORT;
	private int TIME_OUT;
	public static int MSG_SIZE;


	private Selector selector = null;
	private ServerSocketChannel sSChannel = null;
	private SelectionKey sKey = null;


	public ChatServer(Handler handler) {
		this(8066, 2048, 1000, handler);
	}

	public ChatServer(int PORT, Handler handler) {
		this(PORT, 2048, 1000, handler);
	}


	public ChatServer(int PORT, int MSG_SIZE, int TIME_OUT, Handler handler) {
		this.PORT = PORT;
		this.TIME_OUT = TIME_OUT;
		this.MSG_SIZE = MSG_SIZE;
		this.handler = handler;
	}

	public void run() {
		try {
			initServer();
			if (sSChannel == null || selector == null || sKey == null) {
				log.info("服务器启动失败");
			} else {
				log.info("服务器启动成功");
				while (true) {
					if (selector.select(TIME_OUT) == 0) {
						continue;
					}
					Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
					while (keyIterator.hasNext()) {
						SelectionKey key = keyIterator.next();
						if (key.isAcceptable()) {
							accept();
						} else if (key.isReadable()) {
							String strMsg = readMsg((SocketChannel) key.channel());
							Message msg = JSON.parseObject(strMsg).toJavaObject(Message.class);
							handler.handle(sessionManager.get((SocketChannel) key.channel()), msg);
						}
						keyIterator.remove();
					}
				}
			}
		} catch (IOException e) {
			log.warning(str("服务器运行错误:%s", e.toString()));
		}
	}

	private void accept() {
		try {
			Session session = new Session(sSChannel.accept(), sKey, id++);
			sessionManager.add(session);
		} catch (IOException e) {
			log.warning(e.toString());
		}
	}

	private String readMsg(SocketChannel channel) {
		try {
			ByteBuffer buffer = ByteBuffer.allocate(MSG_SIZE);
			byte[] data = new byte[MSG_SIZE];
			int i = 0;
			while (channel.read(buffer) > 0) {
				buffer.flip();
				while (buffer.hasRemaining()) {
					data[i++] = buffer.get();
				}
				buffer.clear();
			}
			return new String(data, 0, i, "UTF-8");
		} catch (IOException e) {
			log.warning(e.toString());
			try {
				Session session = sessionManager.remove(channel);
				log.info(str("移除session[id=%d]", session.getSessionId()));
				channel.close();
			} catch (IOException ex) {
				log.warning(e.toString());
			}
			return "{}";
		}
	}

	private void initServer() {
		try {
			selector = Selector.open();
			sSChannel = ServerSocketChannel.open();
			sSChannel.bind(new InetSocketAddress("localhost", PORT));
			sSChannel.configureBlocking(false);
			sKey = sSChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			log.warning(e.toString());
		}
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
