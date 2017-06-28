package boot;

import server.MyServer;

public class Run {

	public static void main(String[] args) {
		
		MyServer server=new MyServer(8888);
		server.start();
	}

}
