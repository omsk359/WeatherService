package service;

import java.util.Timer;
import java.util.TimerTask;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import utils.Log;

@ManagedBean(eager=true)
@ApplicationScoped
public class CronTask {

	public CronTask() 
	{
		Timer timer = new Timer();
		TimerTask task1 = new OpenWeatherUpdater(); 
		timer.schedule(task1, 0, 20000);
		TimerTask task2 = new WeatherComUpdater(); 
		timer.schedule(task2, 0, 15000);
		Log.info("Service started");
	}
}
