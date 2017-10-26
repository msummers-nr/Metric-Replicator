package com.adg.newrelic.copier.task.synthetics;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transform {
  
  private static final Logger log = LoggerFactory.getLogger(Transform.class);
  
  private JSONArray jMonitors;
  private JSONArray jLocations;
  
  private PluginMessage pMessage = new PluginMessage();
  
  public Transform(Extract extract) {

    JSONObject jMonitorData = extract.getMonitorData(); 
    jMonitors = jMonitorData.getJSONArray("facets");
    jLocations = extract.getLocations();
    log.info("Init transform for " + jMonitors.length() + " monitors");
    makeStats();
  }
  
  private void makeStats() {
    // Loop through the monitors
    for(int i=0; i < jMonitors.length(); i++) {
      JSONObject jMonitorData = jMonitors.getJSONObject(i);
      MonitorStats mStats = new MonitorStats(jMonitorData, jLocations);
      mStats.parse();
      pMessage.addMetrics(mStats);
    }
  }
  
  public JSONObject toPluginMessage() {
    return pMessage.getMessage();
  }
  
  public String toInsightsMessage() {
    return null;
  }
}
