package dao;

import dao.Impl.ForecastDAOImpl;

public class Factory {
	private static ForecastDAO forecastDAO = null;
    private static Factory instance = null;
    
    public static synchronized Factory getInstance() {
    	if (instance == null)
    		instance = new Factory();
    	return instance;
    }
    
    public static synchronized ForecastDAO getForecastDAO() {
    	if (forecastDAO == null)
    		forecastDAO = new ForecastDAOImpl();
        return forecastDAO;
    }
}
