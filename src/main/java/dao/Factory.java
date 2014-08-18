package dao;

import dao.Impl.TemperatureDAOImpl;

public class Factory {
	private static TemperatureDAO forecastDAO = null;
    private static Factory instance = null;
    
    public static synchronized Factory getInstance() {
    	if (instance == null)
    		instance = new Factory();
    	return instance;
    }
    
    public static synchronized TemperatureDAO getTemperatureDAO() {
    	if (forecastDAO == null)
    		forecastDAO = new TemperatureDAOImpl();
        return forecastDAO;
    }
}
