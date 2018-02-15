package test.java.timezone.converter;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTimeZone;

import test.java.timezone.util.ExcelUtil;

public class TimeZoneConverter {
	
	public static void main(String [] args)
	{
		
		String fileName = "TimeZones.csv";

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		
		if(!file.exists())
		{
			System.out.println("File not found");
			return;
			
		}
         
  	try {
			ExcelUtil.updateTimeZonesInExcel(file);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
