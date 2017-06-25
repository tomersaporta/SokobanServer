package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import db.Level;
import db.Record;
import db.User;
import model.IModel;
import model.MyModel;
import model.highScore.QueryParams;

public class SokobanClientHandler implements ClientHandler {

	BufferedReader readFromClient;
	PrintWriter writeToClient;
	Gson json;
	IModel model;

	public SokobanClientHandler() {
		this.readFromClient = null;
		this.writeToClient = null;
		GsonBuilder jsonBuilder = new GsonBuilder();
		this.json = jsonBuilder.create();
		this.model=new MyModel();
	}
	
	public SokobanClientHandler(IModel model) {
		this.readFromClient = null;
		this.writeToClient = null;
		GsonBuilder jsonBuilder = new GsonBuilder();
		this.json = jsonBuilder.create();
		this.model=model;
	}

	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) {

		this.readFromClient = new BufferedReader(new InputStreamReader(inFromClient));
		this.writeToClient = new PrintWriter(outToClient);

	}

	public void handleClientCommands() {
		String strCommand;
		Commands cmd;
		String params;

		try {
			strCommand = this.readFromClient.readLine();
			cmd = this.json.fromJson(strCommand, Commands.class);
			System.out.println("Command: " + cmd);
			params= this.readFromClient.readLine();
			
			switch(cmd){
			case ADD_LEVEL:{
				
				
	 			Level level = this.json.fromJson(params, Level.class);
				this.model.addLevel(level);
				
				break;
			}
			case ADD_RECORD:{
				
	 			Record record = this.json.fromJson(params, Record.class);
				this.model.addRecord(record);
				
				break;
			}
			case ADD_USER:{
				
				User user = this.json.fromJson(params, User.class);
				this.model.addUser(user);
				
				break;
			}
			case DB_QUERY:{

				QueryParams qparams=this.json.fromJson(params, QueryParams.class);
				List<Record>records=this.model.dbQuery(qparams);
				
				
				break;
			}
			case GET_SOLUTION:{
				break;
			}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
