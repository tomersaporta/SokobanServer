package model;

import java.util.List;

import db.Level;
import db.Record;
import db.User;
import db.QueryParams;
/**
 * 
 *Defines the behavior that the model needs to implements
 */
public interface IModel {
	
	public List<Record> dbQuery(QueryParams params);
	public void addUser(User user);
	public void addRecord(Record record);
	public void addLevel(Level level);
	public String getSolution(Level level);
	
}
