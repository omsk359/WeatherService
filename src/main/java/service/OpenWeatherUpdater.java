package service;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dao.Factory;
import dao.TemperatureItem;
import dao.Impl.TemperatureItemOW;
import utils.Log;

public class OpenWeatherUpdater extends TimerTask
{
	@Override
	public void run()
	{
		Log.info("OpenWeatherUpdater starting...");
		try {
			List<TemperatureItem> forecast = download();
			Log.info(String.format("Recieved %d items", forecast.size()));
			Factory.getTemperatureDAO().addOrUpdate("OpenWeather", forecast);
			
			// remove old data
			Factory.getTemperatureDAO().removeEarly("Open Weather", new Date());
		} catch (Exception e) {
			Log.error(e);
		}
	}
	
	private List<TemperatureItem> download() throws Exception
	{
		URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=Saint%20Petersburg&mode=xml");
		InputStream stream = url.openStream();

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(stream);
		NodeList nodes = doc.getElementsByTagName("time");
		List<TemperatureItem> forecast = new ArrayList<TemperatureItem>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Element e = (Element) nodes.item(i);
	    	String dateFromStr = e.getAttribute("from");
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	    	Date dateFrom = dateFormat.parse(dateFromStr);
	    	Element temper_e = (Element) e.getElementsByTagName("temperature").item(0);
	    	double temperature = Double.parseDouble(temper_e.getAttribute("value"));

	    	forecast.add(new TemperatureItemOW(dateFrom, temperature));
		}
		return forecast;
	}
}
