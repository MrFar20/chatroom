package pers.mrwangx.tools.chatroom.framework.server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @description:
 * @author: 王昊鑫
 * @create: 2019年08月19 11:00
 **/
public class ChatServerLogger {

	public static final Logger LOGGER = Logger.getLogger("ChatServer");

	static {

	}

	public static void log(Level level, String msg, Throwable e) {
		LOGGER.log(level, msg, e);
	}

	public static void log(Level level, String msg) {
		LOGGER.log(level, msg);
	}

	public static void info(String msg, Throwable e) {
		LOGGER.log(Level.INFO, msg, e);
	}

	public static void info(String msg) {
		LOGGER.info(msg);
	}

	public static void warning(String msg, Throwable e) {
		LOGGER.log(Level.WARNING, msg, e);
	}

	public static void warning(String msg) {
		LOGGER.warning(msg);
	}

	public static void config(String msg, Throwable e) {
		LOGGER.log(Level.CONFIG, msg, e);
	}

	public static void config(String msg) {
		LOGGER.config(msg);
	}


}
