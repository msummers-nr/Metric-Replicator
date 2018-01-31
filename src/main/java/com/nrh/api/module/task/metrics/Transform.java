package com.nrh.api.module.task.metrics;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.model.Event;
import com.nrh.api.module.nr.model.MetricDataModel;
import com.nrh.api.module.nr.model.TimesliceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

public class Transform {

  private static final Logger log = LoggerFactory.getLogger(Transform.class);
  
  private String eventType;

  public Transform(String eventType) {
    this.eventType = eventType;
  }

  /**
   * Convert the metric data into event data
   */
  @Trace
  public ArrayList<Event> toEvents(Map<String, Date> latestMap, ArrayList<MetricDataModel> metricDataList) {
    // this.latestMap = latestMap;
    ArrayList<Event> eventList = new ArrayList<Event>();
    for (MetricDataModel metricDataModel : metricDataList) {
      String uniqueId = metricDataModel.getUniqueId();
      Date latest = latestMap.get(uniqueId);
      eventList.addAll(processMetric(latest, metricDataModel));
    }

    NewRelic.addCustomParameter("metricCopierEvents", eventList.size());
    log.info("Transform complete, we have " + eventList.size() + " events to post");

    return eventList;
  }

  private ArrayList<Event> processMetric(Date latest, MetricDataModel metricDataModel) {
    // Only process timeslices more recent than the latest date
    SortedMap<Date, TimesliceModel> tsMap = metricDataModel.getTimeslicesSince(latest);
    
    // Make an event per timeslice
    ArrayList<Event> eventList = new ArrayList<>();
    for (Date date : tsMap.keySet()) {
      TimesliceModel ts = tsMap.get(date);
      Event e = processTimeslice(date, metricDataModel, ts);
      eventList.add(e);
    }
    return eventList;
  }

  private Event processTimeslice(Date date, MetricDataModel metricDataModel, TimesliceModel ts) {
    Event e = new Event(eventType);
    e.setTimestamp(date);
    
    // Integer attributes
    e.addIntAttribute("appId", metricDataModel.getAppId());
    e.addIntAttribute("hostId", metricDataModel.getHostId());
    e.addIntAttribute("instanceId", metricDataModel.getInstanceId());
    e.addIntAttribute("port", metricDataModel.getPort());

    // String attributes
    e.addStringAttribute("appName", metricDataModel.getAppName());
    e.addStringAttribute("host", metricDataModel.getHost());
    e.addStringAttribute("metricShort", metricDataModel.getShortName());
    e.addStringAttribute("metricFull", metricDataModel.getFullName());
    e.addStringAttribute("uniqueId", metricDataModel.getUniqueId());
    
    // Double precision attributes
    Map<String, Double> valueMap = ts.getValueMap();
    for (String key : valueMap.keySet()) {
      e.addNumericAttribute(key, valueMap.get(key));
    }
    return e;
  }
}