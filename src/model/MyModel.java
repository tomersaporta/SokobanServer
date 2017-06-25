package model;

import java.util.List;

import db.Level;
import db.Record;
import db.User;
import model.highScore.DbManager;
import model.highScore.QueryParams;
import model.sokobanSolver.SokobanSolver;

public class MyModel implements IModel{
	
	private DbManager dbManager;
	private SokobanSolver solver;
	
	public MyModel() {
		this.dbManager=DbManager.getInstance();
	}

	@Override
	public List<Record> dbQuery(QueryParams params) {
		return dbManager.recordsQuery(params);
	}

	@Override
	public void addUser(User user) {
		if(!dbManager.isUserExistInTable(user.getName())){
			dbManager.add(user);
		}
	}

	@Override
	public void addRecord(Record record) {
		dbManager.add(record);
	}

	@Override
	public void addLevel(Level level) {
		if(!dbManager.isLevelExistInTable(level.getLevelID()))
			dbManager.add(level);
	}

	@Override
	public String getSolution(Level level) {
		
		//to compress the solution
		return this.solver.solveLevel(level).toString();
	}

}
