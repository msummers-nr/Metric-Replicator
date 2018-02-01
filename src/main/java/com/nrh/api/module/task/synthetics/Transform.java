package com.nrh.api.module.task.synthetics;

import com.nrh.api.module.nr.model.Event;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transform {
  
  private static final Logger log = LoggerFactory.getLogger(Transform.class);
  
  private JSONArray jMonitors;
  private JSONArray jLocations;
  
  private DataFormatPlugin dfPlugin = new DataFormatPlugin();
  private DataFormatInsights dfInsights = new DataFormatInsights();
  
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
      
      // This will convert the data for this monitor into a standard format
      DataConverter converter = new DataConverter(jMonitorData, jLocations);
      
      // Add the data for that monitor to the Plugin Formatter
      dfPlugin.addMonitorData(converter);
      
      // Add the data for that monitor to the Insights Formatter
      dfInsights.addMonitorData(converter);
    }
  }
  
  public JSONObject toPluginFormat() {
    JSONObject jPlugin = dfPlugin.getJSON();
//    log.debug(jPlugin.toString(2));
    return jPlugin;
  }
  
  public ArrayList<Event> toInsightsFormat() {
    // JSONArray jInsights = dfInsights.getJSON();
    // log.debug(jInsights.toString(2));
    // return jInsights;
    return dfInsights.getEventList();
  }
}
