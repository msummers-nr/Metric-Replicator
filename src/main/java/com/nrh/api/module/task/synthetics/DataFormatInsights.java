package com.nrh.api.module.task.synthetics;

import com.nrh.api.module.nr.model.Event;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class DataFormatInsights {

  // private JSONArray jArr = new JSONArray();
  private ArrayList<Event> eventList = new ArrayList<>();

  public void addMonitorData(DataConverter converter) {
    Event e = new Event("ExtraSyntheticsInfo");
    e.setTimestamp(new Date());
    e.addStringAttribute("monitorName", converter.getMonitorName());
    
    // Add all of the metrics from the Insights map
    Map<String, Double> mInsightsMetrics = converter.getInsightsMap();
    Iterator<String> iter = mInsightsMetrics.keySet().iterator();
    while (iter.hasNext()) {
      String sMetricName = iter.next();
      Double dMetricValue = mInsightsMetrics.get(sMetricName);
      e.addDoubleAttribute(sMetricName, dMetricValue);
    }
    
    // Add this newly created event to the array
    eventList.add(e);
  }
  
  public ArrayList<Event> getEventList() {
    return eventList;
  }
}
