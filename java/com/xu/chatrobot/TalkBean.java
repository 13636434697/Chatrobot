package com.xu.chatrobot;

/**
 * 提问和回答的对象封装
 *
 * 提问和回答都要用这个对象，所以添加了一个标记
 */
public class TalkBean {

	//构造方法
	public TalkBean(String content, boolean isAsk, int imageId) {
		super();
		this.content = content;
		this.isAsk = isAsk;
		this.imageId = imageId;
	}

	public String content;//谈话的内容

	public boolean isAsk;// 标记是否是提问

	public int imageId;// 回答图片的id
}
