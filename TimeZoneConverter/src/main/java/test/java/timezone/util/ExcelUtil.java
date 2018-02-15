package test.java.timezone.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import test.java.timezone.converter.TimeZoneConverter;
import test.java.timezone.converter.TimeZoneLookup;
import test.java.timezone.converter.TimeZoneResult;

public class ExcelUtil {
	
	public static void updateTimeZonesInExcel(File file) throws InvalidFormatException, IOException
	{
			
		  // Creating a workbook from an Excel file 
	    Workbook workbook = WorkbookFactory.create(file);
	    
	
	    // Create output stream to use for updating the excel
	    FileOutputStream fos = new FileOutputStream(file, true);

	    // Getting the first sheet in the workbook
	    Sheet sheet = workbook.getSheetAt(0);

	    // Create a DataFormatter to format the data
	    DataFormatter dataFormatter = new DataFormatter();

 	   	Date date = null;
    	Double lat = 0D;
    	Double lang = 0D;
    	
    	// iterating over the rows and columns
    	
        for (Row row: sheet) {
        	System.out.println("Processing row:" + row.getRowNum());
            for(Cell cell: row) {
                String cellValue = dataFormatter.formatCellValue(cell);

                switch(cell.getColumnIndex())
            	{
            		case 0:
	            			 date =  DateUtil.getJavaDate(cell.getNumericCellValue());
	            			break;
            			
            		case 1:
            				 lat = Double.valueOf(cellValue);
            				break;
            				
              		case 2:
	            			 lang = Double.valueOf(cellValue);
	        				break;
            			
            		default:
            			break;
            	}
              }
            
            String timeZone = getTimeZoneString(lat,lang);
            // System.out.println(new DateTime(utctime).toLocalDateTime());
            long utctime = convertToLocalTime(date.getTime(), lat, lang);
            String localTime = (new DateTime(utctime)).toLocalDateTime().toString();
        
            int col = row.getLastCellNum();
            Cell cell = row.createCell(col);
            cell.setCellValue(timeZone);
            
            col = row.getLastCellNum();
            cell = row.createCell(col);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(localTime);
 
        }
        
        System.out.println("Finished, please check the updated excel in the target/classes folder of the project");
        
        workbook.write(fos);
        workbook.close();
        
        
     
	}
	
	public static long convertToLocalTime(long utcDate, double latitude, double longitude) {
	    TimeZoneLookup timeZoneLookup = new TimeZoneLookup();
	    TimeZoneResult timeZoneResult = timeZoneLookup.getTimeZone(latitude, longitude);
	    DateTimeZone zone = DateTimeZone.forID(timeZoneResult.getResult());
	    return zone.convertUTCToLocal(utcDate);
	  }
	
	public static String getTimeZoneString(double latitude, double longitude)
	{
		TimeZoneLookup timeZoneLookup = new TimeZoneLookup();
		TimeZoneResult timeZoneResult = timeZoneLookup.getTimeZone(latitude, longitude);
		return timeZoneResult.getResult();
	}

}
