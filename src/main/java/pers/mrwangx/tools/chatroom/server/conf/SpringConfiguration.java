package pers.mrwangx.tools.chatroom.server.conf;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import pers.mrwangx.tools.chatroom.server.ChatServer;
import pers.mrwangx.tools.chatroom.handler.Handler;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 14:49
 ***/
@Configuration
@ComponentScan(basePackages = "pers.mrwangx.tools.chatroom.server.*")
@PropertySource("classpath:settings.prop")
public class SpringConfiguration {

	@Resource
	@Lazy
	private Handler handler;

	@Value("${corePoolSize}")
	private int corePoolSize;
	@Value("${maximumPoolSize}")
	private int maximumPoolSize;
	@Value("${keepAliveTime}")
	private long keepAliveTime;
	@Value("${queueLength}")
	private int queueLength;


	@Bean
	public ExecutorService excutorPool() {
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueLength));
	}

	@Bean
	public ChatServer chatServer() {
		return new ChatServer(handler);
	}
	@Bean
	public Logger logger() {
		return Logger.getLogger("chatServer");
	}

}
