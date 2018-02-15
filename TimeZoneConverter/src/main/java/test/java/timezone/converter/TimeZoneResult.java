package test.java.timezone.converter;

import java.util.ArrayList;
import java.util.List;

public class TimeZoneResult {
  private String result;
  private List<String> alternativeResults;

  public TimeZoneResult(String result)
  {
    this(result, new ArrayList<String>());
  }

  public TimeZoneResult(String result, List<String> alternateResults)
  {
    this.result = result;
    this.alternativeResults = alternateResults;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public List<String> getAlternativeResults() {
    return alternativeResults;
  }

  public void setAlternativeResults(List<String> alternativeResults) {
    this.alternativeResults = alternativeResults;
  }
}
