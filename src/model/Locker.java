package model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 
 * Singleton Locker which helps to synchronized actions from some threads 
 */
public class Locker {

	private static class LockHolder{
		public static final Lock locker = new ReentrantLock();
	}
	
	private static class LockerHolder{
		public static final Locker instance = new Locker();
	}
	
	public static Lock getLocker() {
		return LockHolder.locker;
	}
	
	public static Locker getInstance() {
		return LockerHolder.instance;
	}
}
