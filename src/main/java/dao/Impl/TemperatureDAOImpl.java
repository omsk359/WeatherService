package dao.Impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import utils.HibernateUtil;
import utils.Log;
import dao.TemperatureDAO;
import dao.TemperatureItem;

public class TemperatureDAOImpl implements TemperatureDAO 
{
	public void addOrUpdate(String provider, List<TemperatureItem> items) throws SQLException
	{
        Session session = null;
        try {
    		Log.info(String.format("Save temp(%d) to DB(%s)...", items.size(), provider));
            session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            for (int i = 0; i < items.size(); i++) {
            	session.saveOrUpdate(items.get(i));
            	if (i % 20 == 0) {
            		session.flush();
            		session.clear();
            	}
            }
            tx.commit();
            Log.info("Success!");
        } catch (Exception e) {
        	Log.error(e);
//       		throw e;
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
	}

	@SuppressWarnings("unchecked")
	public List<TemperatureItem> getAll(String provider) throws SQLException
	{
		Log.info(String.format("Get temperature from DB for %s...", provider));
        Session session = null;
        List<TemperatureItem> items = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            if (provider.equals("Open Weather"))
            	items = (List<TemperatureItem>) 
            				session.createCriteria(TemperatureItemOW.class).list();
            else if (provider.equals("weather.com"))
            	items = (List<TemperatureItem>) 
            				session.createCriteria(TemperatureItemWC.class).list();
            Log.info(String.format("Found %d items!", items.size()));
        } catch (Exception e) {
        	Log.error(e);
//       		throw e;
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return items;
	}

	@SuppressWarnings("serial")
	private static Map<String, String> providerTables = new HashMap<String, String>() {{
		put("Open Weather", "TemperatureItemOW");
		put("weather.com", "TemperatureItemWC");
	}};
	
	public void removeEarly(String provider, Date date) throws SQLException
	{
		Log.info(String.format("Remove temperature from DB for %s...", provider));
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
        	String hql = "delete from " + providerTables.get(provider) + " where moment < :date"; 
        	int result = session.createQuery(hql).setTimestamp("date", date).executeUpdate();
            Log.info(String.format("Removed %d items!", result));
        } catch (Exception e) {
        	Log.error(e);
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
	}
}
