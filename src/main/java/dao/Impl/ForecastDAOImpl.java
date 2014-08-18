package dao.Impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import utils.HibernateUtil;
import utils.Log;
import dao.ForecastDAO;
import dao.TemperatureItem;

public class ForecastDAOImpl implements ForecastDAO 
{
	public void addTemperature(String provider, List<TemperatureItem> items) throws SQLException
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
	public List<TemperatureItem> getTemperature(String provider) throws SQLException
	{
		Log.info(String.format("Get temperature from DB for %s...", provider));
        Session session = null;
        List<TemperatureItem> items = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            if (provider.equals("OpenWeather"))
            	items = (List<TemperatureItem>) 
            				session.createCriteria(TemperatureItemOW.class).list();
            else if (provider.equals("WeatherCom"))
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
}
