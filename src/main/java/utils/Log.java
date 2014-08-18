package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Log {
	static private PrintWriter servlet_out = null;
	static private File logf = new File("weather_service.log");
	static private PrintWriter writer = null;
	static private SimpleDateFormat d_format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	static {
		d_format.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
		try {
			writer = new PrintWriter(new FileOutputStream(logf, true /* append = true */));
		} catch (FileNotFoundException e) {}
	}
	static private int MAX_SIZE = 20 * 1024 * 1024;

	// TODO: like "tail -f"
	static private void check_clear()
	{
		try {
			if (logf.length() > MAX_SIZE) { // clear
				if (writer != null)
					writer.close();
				writer = new PrintWriter(logf);
				writer.print("Cleared\n");
				writer.close();
				writer = null;
			}
			if (writer == null)
				writer = new PrintWriter(new FileOutputStream(logf, true));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	static public void info(String s)
	{
		System.out.println(s);
		if (servlet_out != null) {
			servlet_out.println(s.replace("\n", "<br>\n") + "<br>");
			servlet_out.flush();
		}
		check_clear();
		writer.format("%s: %s\n", d_format.format(new Date()), s);
		writer.flush();
	}
	
	static public void error(String s)
	{
		System.out.println(s);
		if (servlet_out != null) {
			servlet_out.println(s.replace("\n", "<br>\n") + "<br>");
			servlet_out.flush();
		}
		check_clear();
		writer.format("ERROR: %s: %s\n", d_format.format(new Date()), s);
		writer.flush();
	}
	static public void error(Exception e) { error(ex2str(e)); }

	static public String ex2str(Exception e)
	{
		String stackTrace = e.toString();
		for (StackTraceElement ste : e.getStackTrace())
			stackTrace += ste + "\n";
		return stackTrace;
	}
}
