package model.highScore;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import db.Level;
import db.Record;
import db.User;

public class DbManager {
	private static DbManager instance = new DbManager();

	public static DbManager getInstance() {
		return instance;
	}

	private SessionFactory factory;

	private DbManager() {
		// to show the severe msg
		Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);

		// reading the xml so he can connect to the Db
		Configuration configuration = new Configuration();
		configuration.configure();
		factory = configuration.buildSessionFactory();
	}

	public void add(Object obj) {
		Session session = null;
		Transaction tx = null;

		try {
			session = factory.openSession();
			tx = session.beginTransaction();

			session.save(obj);
			tx.commit();
		} catch (HibernateException ex) {
			if (tx != null)
				tx.rollback();
			System.out.println(ex.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
	}

	public List<Record> recordsQuery(QueryParams params) {
		Session session = null;
		//Record record = null;
		Query query = null;
		List<Record> list = null;

		try {
			session = factory.openSession();
			if (params.getLevelId().equals("") && params.getUserName().equals("")) {
				query = session.createQuery("from Records as rec ORDER BY rec." + params.getOrderBy());
			} else if (!(params.getLevelId().equals("")) && !(params.getUserName().equals(""))) {
				query = session.createQuery("from Records as rec where rec.levelId=:levelId "
						+ "and rec.userName=:userName ORDER BY rec." + params.getOrderBy());
				query.setParameter("levelId", params.getLevelId());
				query.setParameter("userName", params.getUserName());
			} else if (!params.getLevelId().equals("")) {
				query = session.createQuery(
						"from Records as rec where rec.levelId=:levelId " + "ORDER BY rec." + params.getOrderBy());
				query.setParameter("levelId", params.getLevelId());
			} else if (!params.getUserName().equals("")) {
				query = session.createQuery(
						"from Records as rec where rec.userName=:userName " + "ORDER BY rec." + params.getOrderBy());
				query.setParameter("userName", params.getUserName());
			}

			query.setMaxResults(15);
			list = query.getResultList();
//			Iterator<Record> it = list.iterator();

//			while (it.hasNext()) {
//				record = it.next();
//				System.out.println(record);
//			}

		} catch (HibernateException ex) {
			System.out.println(ex.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
		return list;

	}

	public boolean isLevelExistInTable(String levelId) {
		Session session = null;
		Query query = null;
		List<Level> list = null;
		try {
			session = factory.openSession();
			query = session.createQuery("from Levels as l where l.levelID=:levelID");
			query.setParameter("levelID", levelId);

			list = query.getResultList();
			if (list.size() > 0)
				return true;

		} catch (HibernateException ex) {
			System.out.println(ex.getMessage());
		} finally {
			if (session != null)
				session.close();
		}

		return false;
	}
	public boolean isUserExistInTable(String userName) {
		Session session = null;
		Query query = null;
		List<User> list = null;
		try {
			session = factory.openSession();
			query = session.createQuery("from Users as u where u.name=:name");
			query.setParameter("name", userName);

			list = query.getResultList();
			if (list.size() > 0)
				return true;

		} catch (HibernateException ex) {
			System.out.println(ex.getMessage());
		} finally {
			if (session != null)
				session.close();
		}

		return false;
	}

	public void close() {
		factory.close();
	}

}
