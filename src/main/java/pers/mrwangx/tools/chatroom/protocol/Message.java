package pers.mrwangx.tools.chatroom.protocol;

/***
 * @author 王昊鑫
 * @description
 * @date 2019年08月08日 14:20
 ***/
public class Message {

	public static final int ID = 0;
	public static final int MESSAGE = 1;
	public static final int CMD = 2;
	public static final int UPDATE_NAME = 3;

	private int type;
	private int fromId;
	private String name;
	private String content;
	private long time;
	private int toId;

	@Override
	public String toString() {
		return "Message{" +
						"type=" + type +
						", fromId=" + fromId +
						", content='" + content + '\'' +
						", time=" + time +
						", toId=" + toId +
						'}';
	}

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
