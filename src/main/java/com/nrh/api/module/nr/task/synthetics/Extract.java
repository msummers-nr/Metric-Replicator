package com.nrh.api.module.nr.task.synthetics;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nrh.api.module.nr.Insights;

public class Extract {
  private static final Logger log = LoggerFactory.getLogger(Extract.class);
  private static final String NRQL_UNIQUES = "SELECT uniques(locationLabel) FROM SyntheticCheck";
  private Insights insights;
  private JSONArray jLocations;
  private JSONObject jMonitorData;
  
  public Extract(Insights insights) throws IOException {
    this.insights = insights;
    queryLocationData();
    queryMonitorData();
  }
  
  public JSONArray getLocations() {
    return jLocations;
  }
  
  public JSONObject getMonitorData() {
    return jMonitorData;
  }
  
  private void queryLocationData() throws IOException {
    // Get the list of unique locations
    String sUniques = insights.querySync(NRQL_UNIQUES);
    jLocations = NrqlHelper.parseUniques(sUniques);
    log.info("Location count: " + jLocations.length());
  }
  
  private void queryMonitorData() throws IOException {
    // Run the filter() query to get monitor data by every location
    String nrqlFilterQuery = NrqlHelper.makeFilterQuery(jLocations);
    String sMonitorData = insights.querySync(nrqlFilterQuery);
    jMonitorData = new JSONObject(sMonitorData);
  }
}
