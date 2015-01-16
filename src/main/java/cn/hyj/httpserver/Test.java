package cn.hyj.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
	private static final int SERVER_PORT = 80; //服务器端口
	
	public void startServer(int port){
		try {
			System.out.println("now start http server...");
			System.out.println("the server port is ["+ SERVER_PORT +"]");
			ServerSocket serverSocket = new ServerSocket(port);
			while(true){
				Socket socket = serverSocket.accept();
				new HttpServer(socket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Test().startServer(SERVER_PORT);
	}
}
