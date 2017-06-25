package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SokoClientHandler extends Observable implements ClientHandler {

	private BlockingQueue<String> msgToClient;
	private boolean stopSendToClient;
	
	public SokoClientHandler() {
		this.msgToClient=new ArrayBlockingQueue<String>(20);
		this.stopSendToClient=false;
		this.msgToClient.clear();
	}
	
	public BlockingQueue<String> getMsgToClient() {
		return msgToClient;
	}

	public void setMsgToClient(BlockingQueue<String> msgToClient) {
		this.msgToClient = msgToClient;
	}
	
	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) {
		
	
		try {
			this.msgToClient=new ArrayBlockingQueue<String>(20);
			this.msgToClient.clear();
			this.stopSendToClient=false;
			//Adapter from InputStream to BufferReader
			BufferedReader clientInput=new BufferedReader(new InputStreamReader(inFromClient));
			PrintWriter serverOutput=new PrintWriter(outToClient);
			
			//open a new thread who reading from the client
			Thread fromClient=aSyncReadInputs(clientInput, "exit");
			Thread toClient=aSyncSendOutput(serverOutput);
			
			
			fromClient.join();
			toClient.join();
			clientInput.close();
			serverOutput.close();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//read from client
	private void readInputs(BufferedReader in, String exitStr){
		
		String line;
		boolean flag=false;
		
		try {
			while(!flag){
				
				 line=in.readLine();
				 
				 if(line.equals(exitStr)){
					flag=true;
					insertToQueue("bye");
					stop();
				}
				else{
					setChanged();
					notifyObservers(line);
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Thread aSyncReadInputs(BufferedReader in, String exitStr){
		
		Thread t=new Thread(new Runnable() {
			
			@Override
			public void run() {
				readInputs(in, exitStr);
			}
		});
		t.start();
		return t;
		
	}

	
	
	//send to client
	public void sendOutput(PrintWriter out){
		String str;
		while(!this.stopSendToClient){
			try {
				
				//str=getMsgToClient().poll(1, TimeUnit.SECONDS);
				str=getMsgToClient().take();
				out.println(str);
				out.flush();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	public Thread aSyncSendOutput(PrintWriter out){
		Thread t=new Thread(new Runnable() {
			
			@Override
			public void run() {
				sendOutput(out);
			}
		});
		t.start();
		return t;
	}
	
	
	public void insertToQueue(String str){

		try {
			this.msgToClient.put(str);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendLevel(char[][] levelBored){
		
		for(int i=0;i<levelBored.length;i++){
			String str=String.valueOf(levelBored[i]);
			insertToQueue(str);
		}
		
	}
	
	private void stop(){
		this.stopSendToClient=true;
	}
	
}
