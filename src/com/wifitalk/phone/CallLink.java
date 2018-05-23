package com.wifitalk.phone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class CallLink {

	final int TALK_PORT = 22222;

	String ipAddr = null;
	Socket outSock = null;
	ServerSocket inServSock = null;
	Socket inSock = null;

	CallLink(String inIP) {
		ipAddr = inIP;
	}

	void open() throws IOException, UnknownHostException {// 打开网路连接
		if (ipAddr != null)
			outSock = new Socket(ipAddr, TALK_PORT);
	}

	void listen() throws IOException {// 监听,等候呼叫
		inServSock = new ServerSocket(TALK_PORT);
		inSock = inServSock.accept();
	}

	public InputStream getInputStream() throws IOException {// 返回音频数据输入流
		if (inSock != null)
			return inSock.getInputStream();
		else
			return null;
	}

	public OutputStream getOutputStream() throws IOException {// 返回音频数据输出流
		if (outSock != null)
			return outSock.getOutputStream();
		else
			return null;
	}

	void close() throws IOException {// 关闭网络连接
		inSock.close();
		outSock.close();
	}

}
