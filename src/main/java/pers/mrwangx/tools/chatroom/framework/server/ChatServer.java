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

import static pers.mrwangx.tools.chatroom.framework.util.StringUtil.*;


/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月10 16:56
 **/
public abstract class ChatServer<T extends Message> extends Thread {

	private static final Logger log = Logger.getLogger("ChatServer");



	public static int MSG_SIZE;					//单次消息大小
	private String host;						//绑定本地的主机名
	private int port;							//绑定端口
	private int timeout;						//selector的timeout
	protected AtomicInteger currentSessionId;   //当前session的ID

	protected SessionManager sessionManager;    //sessionManager 用于管理存储session
	protected Handler handler;					//处理每次消息的handler 会调用handle(session, message)方法

	protected Selector selector = null;
	protected ServerSocketChannel sSChannel = null;   //服务器的channel
	protected SelectionKey sKey = null;

	protected ExecutorService executorService;   //用于处理信息的线程池 构造方法不指定默认 最大处理量为400 队列长度为200 队列满之后会拒绝处理

	public ChatServer(String host, int port, int timeout, int initSessionId, SessionManager sessionManager, Handler handler, int MSG_SIZE) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.currentSessionId = new AtomicInteger(initSessionId);
		this.sessionManager = sessionManager;
		this.handler = handler;
		this.MSG_SIZE = MSG_SIZE;
		this.executorService = new ThreadPoolExecutor(200, 400, 1000, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(200));
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
			initServer(); //初始化服务器
			if (sSChannel == null || selector == null || sKey == null) {
				log.info(str("服务器[%s:%d]启动失败", host, port));
			} else {
				log.info(str("服务器[%s:%d]启动成功", host, port));
				while (true) {
					if (Thread.interrupted()) {
						break;
					}
					try {
						if (selector.select(timeout) == 0) {
							continue;
						}
					} catch (IOException e) {
						log.warning(str("服务器运行错误:%s", e.getMessage()));
						continue;
					}
					Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
					while (keyIterator.hasNext()) {
						SelectionKey key = keyIterator.next();
						if (key.isAcceptable()) { //有人连接
							SocketChannel s = null;
							try {
								s = sSChannel.accept();
							} catch (IOException e) {
								log.warning(str("服务器运行错误:%s", e.getMessage()));
							}
							if (s != null) {
								accept(s);
							}
						} else if (key.isReadable()) { //开始处理消息
							byte[] data = readData((SocketChannel) key.channel()); //读取字节数据
							executorService.execute(() -> {
								T msg = null;
								try {
									msg = parseMessage(data);						   //将字节流数据转换为自定义的信息
								} catch (Exception e) {
									log.warning(str("服务器运行错误:%s", e.getMessage()));
								}
								if (msg != null) {
									//调用handler处理信息
									handler.handle(sessionManager.get((SocketChannel) key.channel()), msg);
								}
							});
						}
						keyIterator.remove();
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.warning(str("服务器终止运行:%s", e.getMessage()));
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


	/**
	 * 将读取的数据转换为自定义的Message对象
	 * @param data
	 * @return
	 */
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
