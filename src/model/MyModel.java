package model;

import java.util.List;
import java.util.concurrent.locks.Lock;

import db.Level;
import db.QueryParams;
import db.Record;
import db.User;
import model.highScore.DbManager;
import model.sokobanSolver.SokobanSolver;
import search.Action;

/**
 *Implements the methods IModel defines
 *The specific model to our Sokoban server 
 */
public class MyModel implements IModel{
	
	private DbManager dbManager;
	private SokobanSolver solver;
	private Lock lock;
	
	public MyModel(Lock lock) {
		this.solver=new SokobanSolver();
		
		this.dbManager=DbManager.getInstance();
		
		this.lock=lock;
	}

	/**
	 * Asking the DB about the records according to the query parameters
	 */
	@Override
	public List<Record> dbQuery(QueryParams params) {
		return dbManager.recordsQuery(params);
	}

	/**
	 * Adding user to the DB
	 */
	@Override
	public void addUser(User user) {
		if(!dbManager.isUserExistInTable(user.getName())){
			dbManager.add(user);
			System.out.println("the user was added");
		}
		lock.unlock();
	}

	/**
	 * Adding record to the DB
	 */
	@Override
	public void addRecord(Record record) {
		dbManager.add(record);
	}

	/**
	 * Adding level to the DB
	 */
	@Override
	public void addLevel(Level level) {
		if(!dbManager.isLevelExistInTable(level.getLevelID()))
			dbManager.add(level);
	}

	/**
	 * Asking solution from sokoban service(REST server).
	 * If the solution exists in the rest- return it to the client.
	 * Else, calculate the solution and send it to the rest server
	 */
	@Override
	public String getSolution(Level level) {
		List<Action> sol=this.solver.solveLevel(level);
		StringBuilder sb= new StringBuilder();
		
		for(Action a:sol){
		switch(a.getAction()){
			case "right": sb.append("r");
			  		      break;
			case "left": sb.append("l");
		      			  break;
			case "up": sb.append("u");
		                  break;
			case "down": sb.append("d");
		      			  break;
			}
		}
		
		System.out.println("sol: "+sb.toString());
			
		return sb.toString();
	}
	
	

}
