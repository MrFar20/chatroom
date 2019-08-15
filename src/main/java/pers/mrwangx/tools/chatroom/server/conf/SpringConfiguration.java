package pers.mrwangx.tools.chatroom.server.conf;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import pers.mrwangx.tools.chatroom.framework.server.handler.Handler;
import pers.mrwangx.tools.chatroom.framework.server.session.SessionManager;
import pers.mrwangx.tools.chatroom.framework.server.session.SimpleSessionManager;
import pers.mrwangx.tools.chatroom.server.SimpleChatServer;
import pers.mrwangx.tools.chatroom.server.handler.ServerHanler;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 14:49
 ***/
@Configuration
@ComponentScan(basePackages = "pers.mrwangx.tools.chatroom.server.*")
@PropertySource("classpath:settings.prop")
public class SpringConfiguration {


	private Handler handler;
	private SessionManager sessionManager;


	@Value("${corePoolSize}")
	private int corePoolSize;
	@Value("${maximumPoolSize}")
	private int maximumPoolSize;
	@Value("${keepAliveTime}")
	private long keepAliveTime;
	@Value("${queueLength}")
	private int queueLength;

	@Value("${server.host}")
	private String serverHost;
	@Value("${server.port}")
	private int serverPort;
	@Value("${server.timeout}")
	private int serverTimeOut;
	@Value("${server.initSessionId}")
	private int serverInitSessionId;
	@Value("${server.msgSize}")
	private int serverMsgSize;


	@Bean
	public SessionManager simpleSessionManager() {
		return sessionManager = new SimpleSessionManager();
	}

	@Bean
	public Logger logger() {
		return Logger.getLogger("chatServer");
	}

	@Bean
	public Handler handler() {
		return handler = new ServerHanler();
	}

	@Bean
	public ExecutorService excutorPool() {
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueLength));
	}

	@Bean
	public SimpleChatServer chatServer() {
		return new SimpleChatServer(serverHost, serverPort, serverTimeOut, serverInitSessionId, sessionManager, handler, serverMsgSize);
	}

}
