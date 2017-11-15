package com.nrh.api.module.nr.task.synthetics;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataFormatInsights {

  private JSONArray jArr = new JSONArray();

  public void addMonitorData(DataConverter converter) {
    // Setup the event with some basic info
    JSONObject jEvent = new JSONObject();
    jEvent.put("eventType", "ExtraSyntheticsInfo");
    jEvent.put("timestamp", new Date().getTime());
    jEvent.put("monitorName", converter.getMonitorName());
    
    // Add all of the metrics from the Insights map
    Map<String, Double> mInsightsMetrics = converter.getInsightsMap();
    Iterator<String> iter = mInsightsMetrics.keySet().iterator();
    while (iter.hasNext()) {
      String sMetricName = iter.next();
      Double dMetricValue = mInsightsMetrics.get(sMetricName);
      jEvent.put(sMetricName, dMetricValue);
    }
    
    // Add this newly created event to the array
    jArr.put(jEvent);
  }
  
  public JSONArray getJSON() {
    return jArr;
  }
}
