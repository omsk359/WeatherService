package dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface TemperatureDAO {
	public void addOrUpdate(String provider, List<TemperatureItem> items) throws SQLException;
	public List<TemperatureItem> getAll(String provider) throws SQLException;
	public void removeEarly(String provider, Date date) throws SQLException;
}
