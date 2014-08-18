package dao.Impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import dao.TemperatureItem;

@Entity
@Table(name="weathercomtepm")
public class TemperatureItemWC implements TemperatureItem 
{
	private Date date;
	private double temp;

	public TemperatureItemWC() {}
	public TemperatureItemWC(Date d, double t) { date = d; temp = t; }

	@Id
    @Column(name="moment")
	public Date getDate() 		   { return date; }
	public void setDate(Date date) { this.date = date; }
	
	@Column(name="temp")
	public double getTemp() 		 { return temp; }
	public void setTemp(double temp) { this.temp = temp; }
}
