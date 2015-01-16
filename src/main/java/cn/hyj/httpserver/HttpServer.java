package cn.hyj.httpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * java版的http web服务器实现(多线程)
 * 
 * @author kin wong
 */
public class HttpServer  extends Thread {
	private final static String WEB_ROOT = "D:/soft/develop/Tomcat/apache-tomcat-6.0.20/webapps/ROOT"; //工程目录
	
	private Socket socket;
	private InputStream in;
	private PrintStream out;
	
	/**
	 * 初始化(传入socket对象，初始化输入、输出管道流对象)
	 * @param socket
	 */
	public HttpServer(Socket socket){
		this.socket = socket;
		try {
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行线程
	 * 1、解析请求的文件名
	 * 2、发送(返回)文件内容
	 */
	public void run(){
		String filename = parse(in);
		sendFile(filename);
	}

	/**
	 * 发送文件
	 * @param filename 文件名
	 */
	private void sendFile(String filename) {
		File file = new File(HttpServer.WEB_ROOT + filename);
		
		//找不到文件返回404错误
		if(!file.exists()){
			sendErrorMessage(400, "File Not Found");
		}
		
		//组装HTTP响应信息：状态行+【响应头】+响应正文
		try {
			InputStream in = new FileInputStream(file);
			byte content[] = new byte[(int)file.length()];
			in.read(content);
			out.println("HTTP/1.0 200 Query File");
			out.println("content-length:"+content.length);
			out.println();
			out.write(content);
			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}

	/**
	 * 接收http请求，解析请求行并得出请求的文件名
	 * @param in 字节输入流
	 * @return 请求的文件名
	 */
	private String parse(InputStream in) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in)); //将字节流转成字符流
		String filename = null;
		try {
			String httMessage = br.readLine();//读取HTTP请求行并分割字段,如: GET /test.html HTTP/1.1
			String[] content = httMessage.split(" ");
			if(content.length!=3){
				sendErrorMessage(400,"Clinet query error!");
				return null;
			}
			System.out.println("code: "+content[0]+" ,filename:"+content[1]+" ,http version:"+content[2]); //打印请求行
			filename = content[1];
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return filename;
	}

	/**
	 * 发送http错误信息
	 * @param errorCode 错误码
	 * @param errorMessage 错误信息
	 */
	private void sendErrorMessage(int errorCode, String errorMessage) {
		out.println("HTTP/1.0 "+errorCode+" "+errorMessage);
		out.println("content-type: text/html");
		out.println();
		out.println("<html>");
		out.println("<title>Error Message");
		out.println("</title>");
		out.println("<body>");
		out.println("<h1>ErrorCode:"+errorCode+",ErrorMeesage:"+errorMessage);
		out.println("</body>");
		out.println("</html>");
		out.flush();
		out.close();
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}