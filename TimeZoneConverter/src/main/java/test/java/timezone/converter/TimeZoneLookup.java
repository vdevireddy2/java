package test.java.timezone.converter;

import com.spatial4j.core.io.GeohashUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TimeZoneLookup {
  private static final TimezoneFileReader tzFile = new TimezoneFileReader();

  public TimeZoneResult getTimeZone(double latitude, double longitude)
  {
    String geohash = GeohashUtils.encodeLatLon(latitude, longitude, 5);
    List<Integer> lineNumber = getTzDataLineNumbers(geohash);
    List<String> timeZones = getTzsFromData(lineNumber);

    if(!timeZones.isEmpty()) {
      TimeZoneResult timeZoneResult = new TimeZoneResult(timeZones.get(0));
      for (int i = 1; i < timeZones.size(); i++) {
        timeZoneResult.getAlternativeResults().add(timeZones.get(i));
      }
      return timeZoneResult;
    }
    int offsetHours = calculateOffsetHoursFromLongitude(longitude);
    return new TimeZoneResult(getTimeZoneId(offsetHours));
  }

  private List<Integer> getTzDataLineNumbers(String geohash)
  {
    int seeked = SeekTimeZoneFile(geohash);
    if (seeked <= 0)
      return new ArrayList<>();

    int min = seeked, max = seeked;
    String seekedGeohash = tzFile.getLine(seeked).substring(0, 5);

    while (true)
    {
      String prevGeohash = tzFile.getLine(min - 1).substring(0, 5);
      if (seekedGeohash == prevGeohash)
        min--;
      else
        break;
    }

    while (true)
    {
      String nextGeohash = tzFile.getLine(max + 1).substring(0, 5);
      if (seekedGeohash == nextGeohash)
        max++;
      else
        break;
    }

    List<Integer> lineNumbers = new ArrayList<>();
    for (int i = min; i <= max; i++)
    {
      int lineNumber = Integer.parseInt(tzFile.getLine(i).substring(5));
      lineNumbers.add(lineNumber);
    }

    return lineNumbers;
  }

  private int SeekTimeZoneFile(String hash)
  {
    int min = 0;
    int max = tzFile.getCount();
    boolean converged = false;

    while (!converged)
    {
      int mid = ((max - min) / 2) + min;
      String midLine = tzFile.getLine(mid);

      for (int i = 0; i < hash.length(); i++)
      {
        if (midLine.charAt(i) == '-')
        {
          return mid;
        }

        if (midLine.charAt(i) > hash.charAt(i))
        {
          max = mid == max ? min : mid;
          break;
        }
        if (midLine.charAt(i) < hash.charAt(i))
        {
          min = mid == min ? max : mid;
          break;
        }

        if (i == 4)
        {
          return mid;
        }

        if (min == mid)
        {
          min = max;
          break;
        }
      }

      if (min == max)
      {
        converged = true;
      }
    }
    return -1;
  }

  private List<String> lookupData;
  private synchronized List<String> getLookupData()
  {
    if(lookupData == null) {
      try {
        lookupData = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
          classLoader.getResourceAsStream("TZL.dat")));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          lookupData.add(line);
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    return lookupData;
  }

  private List<String> getTzsFromData(List<Integer> lineNumbers)
  {
    List<String> lData = getLookupData();
    List<String> data = new ArrayList<>();
    for (Integer lineNumber : lineNumbers) {
      String d = lData.get(lineNumber-1);
      data.add(d);
    }
    return data;
  }

  private int calculateOffsetHoursFromLongitude(double longitude)
  {
    int dir = longitude < 0 ? -1 : 1;
    double posNo = Math.sqrt(Math.pow(longitude, 2));
    if (posNo <= 7.5)
      return 0;

    posNo -= 7.5;
    double offset = posNo / 15;
    if (posNo % 15 > 0)
      offset++;

    return dir * (int)Math.floor(offset);
  }

  private String getTimeZoneId(int offsetHours)
  {
    if (offsetHours == 0)
      return "UTC";

    String reversed = (offsetHours >= 0 ? "-" : "+") + Math.abs(offsetHours);
    return "Etc/GMT" + reversed;
  }
}
