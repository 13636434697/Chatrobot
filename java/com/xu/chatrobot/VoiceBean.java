package com.xu.chatrobot;

import java.util.ArrayList;

/*
* 是声音的javabean，
*
* */
public class VoiceBean {
//很多字段都用不上，就是一个数组ws
	public ArrayList<WsBean> ws;

	//里面有很对对象
	public class WsBean {
		//上面对象里面又一个数组
		public ArrayList<CwBean> cw;
	}
	//上面对象里面又一个数组，数组里面又一个对象
	public class CwBean {
		//对象里面的字段
		public String w;
	}
}
