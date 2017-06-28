package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.locks.Lock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import commands.Commands;
import db.CompressedLevel;
import db.Level;
import db.QueryParams;
import db.Record;
import db.User;
import model.IModel;
import model.MyModel;


public class SokobanClientHandler implements ClientHandler {

	private BufferedReader readFromClient;
	private PrintWriter writeToClient;
	private Gson json;
	private IModel model;
	private Lock locker;

	public SokobanClientHandler(Lock locker) {
		this.readFromClient = null;
		this.writeToClient = null;
		GsonBuilder jsonBuilder = new GsonBuilder();
		this.json = jsonBuilder.create();
		this.locker=locker;
		this.model = new MyModel(this.locker);
		
	}

	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) {

		this.readFromClient = new BufferedReader(new InputStreamReader(inFromClient));
		this.writeToClient = new PrintWriter(outToClient);
		
		handleClientCommands();

	}

	public void handleClientCommands() {
		System.out.println("Time: "+System.currentTimeMillis());
		this.locker.lock();
		
		String strCommand;
		Commands cmd;
		String params;

		try {
			
			strCommand = this.readFromClient.readLine();
			cmd = this.json.fromJson(strCommand, Commands.class);
			
			System.out.println("Command: " + cmd);
			
			params = this.readFromClient.readLine();
			//params=null;

			switch (cmd) {
			case ADD_USER: {

				User user = this.json.fromJson(params, User.class);
				this.model.addUser(user);
				break;
				
			}
			case ADD_LEVEL: {
				this.locker.unlock();
				CompressedLevel compLevel = this.json.fromJson(params, CompressedLevel.class);
				Level level = compLevel.decompressLevel();
				if (level == null)
					break;
				this.model.addLevel(compLevel.decompressLevel());
				break;
			}
			case ADD_RECORD: {
				this.locker.unlock();
				Record record = this.json.fromJson(params, Record.class);
				this.model.addRecord(record);
				break;
			}
			case DB_QUERY: {
				this.locker.unlock();
				QueryParams qparams = this.json.fromJson(params, QueryParams.class);
				
				List<Record>records=this.model.dbQuery(qparams);

				String aJson = this.json.toJson(records);
				this.writeToClient.println(aJson);
				this.writeToClient.flush();
				
				break;
			}
			case GET_SOLUTION: {
				this.locker.unlock();
				CompressedLevel compLevel = this.json.fromJson(params, CompressedLevel.class);

				Level level = compLevel.decompressLevel();
				
				if (level == null) {
					String aJson = this.json.toJson("Error solving level");
					this.writeToClient.println(aJson);
					this.writeToClient.flush();
					break;
				}

				String sol= this.model.getSolution(level);
				
				String aJson = this.json.toJson(sol);
				this.writeToClient.println(aJson);
				this.writeToClient.flush();

				break;
			}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
