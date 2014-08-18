package dao;

import java.util.Date;

public interface TemperatureItem {
	public Date getDate();
	public void setDate(Date date);
	public double getTemp();
	public void setTemp(double temp);
}
