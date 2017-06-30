package model.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;

import commons.SokobanClient;

public class AdminModel {
	
	private static class AdminModelHolder{
		public static final AdminModel instance = new AdminModel();
	} 
	
	public static AdminModel getInstance() {
		return AdminModelHolder.instance;
	}
	
	private Map<Integer,SokobanClient> map;
	
	public AdminModel() {
		map=new HashMap<>();
	}
	
	public void addClient(int id, SokobanClient client) {
		map.put(id, client);
	}
	
	public List<Integer> getClients() {
		List<Integer> clients = new ArrayList<Integer>();
		for (Integer client : map.keySet())
			clients.add(client);
		return clients;
	}
	
	public void disconnectClient(int userId) {
		SokobanClient client = map.get(userId);
		try {
			client.getClientSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
