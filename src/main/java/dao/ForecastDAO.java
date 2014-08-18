package dao;

import java.sql.SQLException;
import java.util.List;

public interface ForecastDAO {
	public void addTemperature(String provider, List<TemperatureItem> items) throws SQLException;
	public List<TemperatureItem> getTemperature(String provider) throws SQLException;
}
