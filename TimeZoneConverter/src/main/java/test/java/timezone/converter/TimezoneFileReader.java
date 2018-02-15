package test.java.timezone.converter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TimezoneFileReader {
  private List<String> tzData;

  private synchronized List<String> getTzData()
  {
    if(tzData == null) {
      try {
        tzData = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
          classLoader.getResourceAsStream("TZ.dat")));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          tzData.add(line);
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    return tzData;
  }

  public int getCount()
  {
    return getTzData().size();
  }

  public String getLine(int line)
  {
    String tzData = getTzData().get(line);
    return tzData;
  }
}
