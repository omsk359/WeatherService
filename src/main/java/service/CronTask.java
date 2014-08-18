package service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import utils.Log;

@ManagedBean(eager=true)
@ApplicationScoped
public class CronTask {

	public CronTask()
	{
		try {
			readConfig();
			Timer timer = new Timer();
			TimerTask updater1 = new OpenWeatherUpdater(); 
			timer.schedule(updater1, 0, delays.get("Open Weather") * 1000);
			TimerTask updater2 = new WeatherComUpdater(); 
			timer.schedule(updater2, 0, delays.get("weather.com") * 1000);
			Log.info("Service started");
		} catch (Exception e) {
			Log.error(e);
		}
	}
	
	private static void readConfig() throws Exception
	{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		Document doc = builder.parse(classloader.getResourceAsStream("config.xml"));
		NodeList nodes = doc.getElementsByTagName("provider");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element item = (Element) nodes.item(i);
			delays.put(item.getAttribute("name"), Integer.parseInt(item.getAttribute("delay")));
		}
	}
		
	private static Map<String, Integer> delays = new HashMap<String, Integer>();
}
