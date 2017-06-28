package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyServer {
	
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
	
	private void runServer()throws Exception { 
		
		ServerSocket server=new ServerSocket(this.port);
		System.out.println("Server alive");
		server.setSoTimeout(1000); 
		
		while(!stop){//we want to wait to the next client- we handle the clients in a line
			try{
				
			Socket aClient=server.accept(); // blocking call
			
				excutor.execute(()->{
					try{
					System.out.println("The client is connected");
					
					InputStream inFromClient=aClient.getInputStream();
					OutputStream outToClient=aClient.getOutputStream();
					
					new SokobanClientHandler(this.locker).handleClient(inFromClient, outToClient);
					inFromClient.close(); 
					outToClient.close(); 
					aClient.close(); 
					}catch(IOException e) {
						//to remove the client from the list of clients
					} 
				});
			
			}catch (SocketTimeoutException e) {}
		} 
		server.close();
	}
	
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

