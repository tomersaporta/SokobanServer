package commons;

import java.net.Socket;

public class SokobanClient {

	private int id;
	private String ip;
	private int port;
	private Socket clientSocket;
	
	public SokobanClient(int id, String ip, int port, Socket clientSocket) {
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.clientSocket = clientSocket;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	
	
	
	
}
