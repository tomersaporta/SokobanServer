package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Sokoban Server.
 * Multi client server.
 * This server handle the records and solution functionality
 *
 */
public class MyServer {
	
	private static int clientId=0;
	
	private static class LockHolder{
		public static final Lock locker = new ReentrantLock();
	}
	
	private int port;
	private volatile boolean stop;
	private ExecutorService excutor;
	private Lock locker;

	public MyServer(int port) {
		this.port=port;
		this.stop=false;
		this.excutor=Executors.newFixedThreadPool(15);
		this.locker=LockHolder.locker;
	}
	
	/**
	 * Hanlde numbers of clients.
	 * Open session to each client
	 * @throws Exception
	 */
	private void runServer()throws Exception { 
		
		ServerSocket server=new ServerSocket(this.port);
		System.out.println("Server alive");
		server.setSoTimeout(1000); 
		
		while(!stop){//we want to wait to the next client- we handle the clients in a line
			try{
				
			Socket aClient=server.accept(); // blocking call
			
				excutor.execute(()->{
					
					try {

						System.out.println("The client is connected");
						new SokobanClientHandler(this.locker).handleClient(++clientId,aClient);
						aClient.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			
			}catch (SocketTimeoutException e) {}
		} 
		server.close();
	}
	
	/**
	 * Put the runServer in a thread
	 */
	public void start(){ 
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					System.out.println("Running");
					runServer();
				}catch (Exception e){e.printStackTrace();}
			} 
		}).start();
	}

	/**
	 * Stopping the server.
	 * 
	 */
	public void stop(){ 
		this.excutor.shutdown();
		try {
			this.excutor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			stop=true;
		}	
	}
	
}

