package service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dao.Factory;
import utils.Log;
import dao.TemperatureItem;
import dao.Impl.*;

public class WeatherComUpdater extends TimerTask
{
	@Override
	public void run()
	{
		Log.info("WeatherComUpdater starting...");
		try {
			List<TemperatureItem> forecast = download();
			Log.info(String.format("Recieved %d items", forecast.size()));
			Factory.getTemperatureDAO().addOrUpdate("weather.com", forecast);
			Factory.getTemperatureDAO().removeEarly("weather.com", new Date());
		} catch (Exception e) {
			Log.error(e);
		}
	}

	private List<TemperatureItem> download() throws Exception
	{
		// weather.com/services
		// Weather Channel and Weather Underground come together to provide API
		URL url = new URL("http://api.wunderground.com/api/effae2c430f85112/hourly/q/RU/Saint_Petersburg.xml");
		InputStream stream = url.openStream();
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(stream);
		NodeList nodes = doc.getElementsByTagName("forecast");
		Calendar calendar = Calendar.getInstance();
		List<TemperatureItem> forecast = new ArrayList<TemperatureItem>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Element e = (Element) nodes.item(i);
			Element epoch_e = (Element) e.getElementsByTagName("epoch").item(0);
	    	long epoch = Long.parseLong(epoch_e.getTextContent());
	    	Date date = new Date(epoch * 1000);
	    	calendar.setTime(date);
	    	if (calendar.get(Calendar.HOUR_OF_DAY) % 3 != 0)
	    		continue;
	    	Element temper_e = (Element) e.getElementsByTagName("temp").item(0);
	    	temper_e = (Element) temper_e.getElementsByTagName("metric").item(0);
	    	double temperature = Double.parseDouble(temper_e.getTextContent());
	    	
	    	forecast.add(new TemperatureItemWC(date, temperature));
		}
		return forecast;
	}
}
