package model.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import commons.SokobanClient;

/**
 *
 * Manage the connections to the server.
 *
 */
public class AdminModel {
	
	private static class AdminModelHolder{
		public static final AdminModel instance = new AdminModel();
	} 
	
	public static AdminModel getInstance() {
		return AdminModelHolder.instance;
	}
	
	private Map<String,SokobanClient> map;
	
	public AdminModel() {
		map=new HashMap<>();
	}
	
	/**
	 * Adding client to the client's list
	 * @param id the id of the client
	 * @param client the object of the client
	 */
	public void addClient(String id, SokobanClient client) {
		map.put(id, client);
	}
	
	/**
	 * 
	 * @return the list of clients
	 */
	public List<String> getClients() {
		List<String> clients = new ArrayList<String>();
		for (String client : map.keySet())
			clients.add(client);
		return clients;
	}
	
	/**
	 * Disconnect client by his id
	 * @param userId the client id
	 */
	public void disconnectClient(String userId) {
		SokobanClient client = map.get(userId);
		
		try {
			client.getClientSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
