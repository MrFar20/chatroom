package pers.mrwangx.tools.chatroom.framework.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import pers.mrwangx.tools.chatroom.framework.protocol.Message;
import pers.mrwangx.tools.chatroom.framework.server.handler.Handler;
import pers.mrwangx.tools.chatroom.framework.server.session.Session;
import pers.mrwangx.tools.chatroom.framework.server.session.SessionManager;

import static pers.mrwangx.tools.chatroom.util.StringUtil.str;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月10 16:56
 **/
public abstract class ChatServer<T extends Message> extends Thread {

	private static final Logger log = Logger.getLogger("ChatServer");

	public static int MSG_SIZE;
	private String host;
	private int port;
	private int timeout;
	protected AtomicInteger currentSessionId;

	protected SessionManager sessionManager;
	protected Handler handler;

	protected Selector selector = null;
	protected ServerSocketChannel sSChannel = null;
	protected SelectionKey sKey = null;

	protected ExecutorService executorService;

	public ChatServer(String host, int port, int timeout, int initSessionId, SessionManager sessionManager, Handler handler, int MSG_SIZE) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.currentSessionId = new AtomicInteger(initSessionId);
		this.sessionManager = sessionManager;
		this.handler = handler;
		this.MSG_SIZE = MSG_SIZE;
		this.executorService = new ThreadPoolExecutor(200, 400, 1000, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(100));
	}

	public ChatServer(String host, int port, int timeout, int initSessionId, SessionManager sessionManager, Handler handler, int MSG_SIZE, ExecutorService executorService) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.currentSessionId = new AtomicInteger(initSessionId);
		this.sessionManager = sessionManager;
		this.handler = handler;
		this.MSG_SIZE = MSG_SIZE;
		this.executorService = executorService;
	}



	@Override
	public void run() {
		try {
			initServer();
			if (sSChannel == null || selector == null || sKey == null) {
				log.info("服务器启动失败");
			} else {
				log.info("服务器启动成功");
				while (!Thread.interrupted()) {
					if (selector.select(timeout) == 0) {
						continue;
					}
					Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
					while (keyIterator.hasNext()) {
						SelectionKey key = keyIterator.next();
						if (key.isAcceptable()) {
							accept(sSChannel.accept());
						} else if (key.isReadable()) {
							byte[] data = readData((SocketChannel) key.channel());
							executorService.execute(() -> {
								T msg = parseMessage(data);
								if (msg != null) {
									handler.handle(sessionManager.get((SocketChannel) key.channel()), msg);
								}
							});
						}
						keyIterator.remove();
					}
				}
			}
		} catch (IOException e) {
			log.warning(str("服务器运行错误:%s", e.toString()));
		}
	}

	private void initServer() {
		try {
			selector = Selector.open();
			sSChannel = ServerSocketChannel.open();
			sSChannel.bind(new InetSocketAddress(host, port));
			sSChannel.configureBlocking(false);
			sKey = sSChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			log.warning(e.toString());
		}
	}

	public abstract T parseMessage(byte[] data);


	public void accept(SocketChannel channel) {
		Session session = new Session(channel, sKey, currentSessionId.incrementAndGet());
		sessionManager.add(session);
	}

	public byte[] readData(SocketChannel channel) {
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
			return data;
		} catch (IOException e) {
			log.warning(e.toString());
			try {
				Session session = sessionManager.remove(channel);
				log.info(str("session[id=%d, name:%s]断开连接,移除", session.getSessionId(), session.getName()));
				channel.close();
			} catch (IOException ex) {
				log.warning(e.toString());
			}
		}
		return null;
	}

	public void shutdown() {
		this.interrupt();
	}

	public boolean isShutDown() {
		return this.isInterrupted();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getCurrentSessionId() {
		return currentSessionId.get();
	}

	public int getMSG_SIZE() {
		return MSG_SIZE;
	}
}
