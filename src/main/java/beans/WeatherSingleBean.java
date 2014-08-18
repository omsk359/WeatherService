package beans;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import dao.Factory;
import dao.TemperatureItem;

@ManagedBean
@SessionScoped
public class WeatherSingleBean {
	
	private String provider;
	
	public String getProvider() { return provider; }
	public void setProvider(String p) { provider = p; }
	
	@SuppressWarnings("serial")
	private static Map<String, String> providerNames = new HashMap<String, String>() {{
		put("Open Weather", "Open Weather");
		put("weather.com", "Weather.com");
	}};	
	public String providerScreenName() { return providerNames.get(provider); }
	
	public List<TemperatureItem> temperature() throws Exception
	{
		return Factory.getTemperatureDAO().getAll(provider);
	}
}
