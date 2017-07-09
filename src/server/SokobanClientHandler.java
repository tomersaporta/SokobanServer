package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import commands.Commands;
import commons.SokobanClient;
import db.CompressedLevel;
import db.Level;
import db.LevelSolutionData;
import db.QueryParams;
import db.Record;
import db.User;
import model.IModel;
import model.MyModel;
import model.admin.AdminModel;

/**
 * Handle Sokoban's clients
 *
 */
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

	/**
	 * Handle the clients command.
	 * Update the AdminModel about the client.
	 */
	@Override
	public void handleClient(int clientId,Socket socket) {
		InputStream inFromClient=null;
		OutputStream outToClient=null;
		
		try {
			inFromClient=socket.getInputStream();
			outToClient=socket.getOutputStream();
			
			this.readFromClient = new BufferedReader(new InputStreamReader(inFromClient));
			this.writeToClient = new PrintWriter(outToClient);
			
			String strClientIdentityJson=this.readFromClient.readLine();
			String  strClientIdentity=this.json.fromJson(strClientIdentityJson, String.class);
			
			strClientIdentity="client "+clientId+" "+strClientIdentity;
			SokobanClient client=new SokobanClient(++clientId, socket.getRemoteSocketAddress().toString(), socket.getPort(), socket);
			
			AdminModel.getInstance().addClient(strClientIdentity, client);
			handleClientCommands();
			//AdminModel.getInstance().disconnectClient(strClientIdentity);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				inFromClient.close();
				outToClient.close();
				this.readFromClient.close();
				this.writeToClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	/**
	 * Handle Sokoban Clients according to the protocol
	 */
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
			case GET_HINT:{
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
				
				//checking if the solution exsits in the DB
				Client client = ClientBuilder.newClient();	
				WebTarget webTarget2 = client.target("http://localhost:8080/RESTSokobenService/SokobanServices/get/"+level.getLevelID());
				Invocation.Builder invocationBuilder2 = webTarget2.request();
				Response response = invocationBuilder2.get();	
				String solutionJson = response.readEntity(String.class);
				System.out.println("from query: "+solutionJson);
				if(solutionJson.equals("null")){
					System.out.println("from solver");
					String sol= this.model.getSolution(level);
					
					String aJson = this.json.toJson(sol);
					this.writeToClient.println(aJson);
					this.writeToClient.flush();
					
					//send the solution to the service
					LevelSolutionData levelSol=new LevelSolutionData(level.getLevelID(), sol, null);
					String solJson=this.json.toJson(levelSol);
					
					WebTarget webTarget = client.target("http://localhost:8080/RESTSokobenService/SokobanServices/add");
					Invocation.Builder invocationBuilder = webTarget.request();
					invocationBuilder.post(Entity.entity(solJson, MediaType.TEXT_PLAIN));

					break;
				}
				else{
					System.out.println("from web service");
					LevelSolutionData levelSol=this.json.fromJson(solutionJson, LevelSolutionData.class);
					String aJson = this.json.toJson(levelSol.getSolution());
					this.writeToClient.println(aJson);
					this.writeToClient.flush();
					break;
				}
			}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
