package beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import service.CronTask;
import utils.Log;
import dao.Factory;
import dao.TemperatureItem;

@ManagedBean
@SessionScoped
public class WeatherBean
{		
	public List<TemperatureItem> temperature(String provider) throws Exception
	{
		return Factory.getTemperatureDAO().getAll(provider);
	}
	
	private List<TableItem> temperatureTable;
	
	public List<TableItem> getTemperatureTable() throws Exception
	{
		if (temperatureTable == null)
			refreshTemperTable();
		return temperatureTable;
	}

	@SuppressWarnings("serial")
	public void refreshTemperTable()
	{
		Map<String, Map<Date, Double>> map;
		try {
			map = new HashMap<String, Map<Date, Double>>() {{
				put("Open Weather", listToMap(temperature("Open Weather")));
				put("weather.com", listToMap(temperature("weather.com")));
			}};
		} catch (Exception e) {
			Log.error(e);
			return;
		}
		Map<Date, Map<String, Double>> tableMap = toTableMap(map);
		
		// Map<Date, Map<String, Double>> -> List<TableItem>
		List<TableItem> result = new ArrayList<TableItem>();
		for (Map.Entry<Date, Map<String, Double>> entry : tableMap.entrySet())
			result.add(new TableItem(entry.getKey(), entry.getValue()));
		
		Collections.sort(result, new Comparator<TableItem>() {
			public int compare(TableItem item1, TableItem item2) {
				return item1.date.compareTo(item2.date);
			}
		});
		temperatureTable = result;
	}
	
	public void update()
	{
		try {
			Log.info("Starting update...");
			CronTask.runImmediately();
			refreshTemperTable();
			Log.info("Update finished!");
		} catch (Exception e) {
			Log.error(e);
		}
	}
	
	// build Map for fast loopup by Date 
	private Map<Date, Double> listToMap(List<TemperatureItem> list)
	{
		Map<Date, Double> result = new HashMap<Date, Double>();
		for (TemperatureItem item : list)
			result.put(item.getDate(), item.getTemp());
		return result;
	}

	// TODO: refact with hql, full outer join
	// Map<String, Map<Date, Double>> -> Map<Date, Map<String, Double>>
	private Map<Date, Map<String, Double>> toTableMap(Map<String, Map<Date, Double>> map)
	{
		Map<Date, Map<String, Double>> tableMap = new HashMap<Date, Map<String, Double>>();
		for (Map.Entry<String, Map<Date, Double>> entry_all : map.entrySet()) {
			Map<Date, Double> provider_map = entry_all.getValue();
			String provider = entry_all.getKey();
			for (Map.Entry<Date, Double> entry_inner : provider_map.entrySet()) {
				Map<String, Double> inner_map = new HashMap<String, Double>();
				Date inner_date = entry_inner.getKey();
				double inner_temper = entry_inner.getValue();
				if (tableMap.containsKey(inner_date))
					continue;
				inner_map.put(provider, inner_temper);
				
				// fill inner_map
				for (Map.Entry<String, Map<Date, Double>> entry_all_inner : map.entrySet())
					if (!provider.equals(entry_all_inner.getKey()) &&
							entry_all_inner.getValue().containsKey(inner_date))
						inner_map.put(entry_all_inner.getKey(), entry_all_inner.getValue().get(inner_date));
//				Log.info("inner: " +  provider + " " + inner_map.size());
				tableMap.put(inner_date, inner_map);
			}
		}
		return tableMap;
	}
	
	public class TableItem {
		public Date date;
		public Date getDate() { return date; }
		public Map<String, Double> tempers;
		
		public TableItem(Date d, Map<String, Double> t) {
			date = d;
			tempers = t;
		}
		
		public String temper(String provider) {
			return tempers.containsKey(provider) ? tempers.get(provider) + "°" : "";
		}
	}
}
