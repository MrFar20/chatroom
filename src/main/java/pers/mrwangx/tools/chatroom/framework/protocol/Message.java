package pers.mrwangx.tools.chatroom.framework.protocol;

/**
 * @description: 信息的格式
 * @author: 王昊鑫
 * @create: 2019年08月10 16:59
 **/
public class Message {

	public static final int ALLOCATE_ID = 0;
	public static final int MESSAGE = 1;
	public static final int CMD = 2;
	public static final int UPDATE_NAME = 3;

	private int type;
	private int fromId;
	private String name;
	private String content;
	private long time;
	private int toId;

	public Message() {}

	public Message(int type, int fromId, String name, String content, long time, int toId) {
		this.type = type;
		this.fromId = fromId;
		this.name = name;
		this.content = content;
		this.time = time;
		this.toId = toId;
	}

	@Override
	public String toString() {
		return "Message{" +
						"type=" + type +
						", fromId=" + fromId +
						", name='" + name + '\'' +
						", content='" + content + '\'' +
						", time=" + time +
						", toId=" + toId +
						'}';
	}

	public static class Builder {

		private int type;
		private int fromId;
		private String name;
		private String content;
		private long time;
		private int toId;

		public Message build() {
			return new Message(type, fromId, name, content, time, toId);
		}

		public Builder type(int type) {
			this.type = type;
			return this;
		}

		public Builder fromId(int fromId) {
			this.fromId = fromId;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder content(String content) {
			this.content = content;
			return this;
		}

		public Builder time(long time) {
			this.time = time;
			return this;
		}

		public Builder toId(int toId) {
			this.toId = toId;
			return this;
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}
}
