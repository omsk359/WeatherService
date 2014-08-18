package service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
public class CronTask
{
	private static Map<Runnable, Integer> delays = new HashMap<Runnable, Integer>();
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public CronTask()
	{
		try {
			readConfig();
			runImmediately();
			Log.info("Service started");
		} catch (Exception e) {
			Log.error(e);
		}
	}
	
	// stop scheduler, run concurrent all updaters, wait, new scheduler
	public static synchronized void runImmediately()
	{
		scheduler.shutdown();
		
		ExecutorService taskExecutor = Executors.newFixedThreadPool(delays.size());
		for (Runnable task : delays.keySet())
			taskExecutor.submit(task);
		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}

		scheduler = Executors.newScheduledThreadPool(1);
		for (Map.Entry<Runnable, Integer> entry : delays.entrySet()) {
			int delay = entry.getValue();
			Runnable task = entry.getKey();
			scheduler.scheduleWithFixedDelay(task, delay, delay, TimeUnit.SECONDS);
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
			int delay = Integer.parseInt(item.getAttribute("delay"));
			switch (item.getAttribute("name")) {
			case "Open Weather":
				delays.put(new OpenWeatherUpdater(), delay);
				break;
			case "weather.com":
				delays.put(new WeatherComUpdater(), delay);
				break;
			}
		}
	}
}
