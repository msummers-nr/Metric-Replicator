package com.nrh.api.module.task.metrics;

import com.nrh.api.module.nr.dao.Application;
import com.nrh.api.module.nr.dao.Event;
import com.nrh.api.module.nr.dao.Metric;
import com.nrh.api.module.nr.dao.Timeslice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

public class Transform {

  private static final Logger log = LoggerFactory.getLogger(Transform.class);
  
  private CopierConfig config;
  private Map<Metric, Date> latestMap;
  private ArrayList<Event> eventList = new ArrayList<Event>();

  public Transform(CopierConfig config) {
    this.config = config;
  }

  /**
   * Remove all the events
   */
  public void clearEvents() {
    eventList.clear();
  }

  /**
   * Convert the metric data into event data
   */
  public ArrayList<Event> toEvents(Map<Metric, Date> latestMap, Map<String, Application> appMap) {
    this.latestMap = latestMap;

    // log.info("Transforming " + appMap.size() + " applications");
    for (String appName : appMap.keySet()) {
      Application app = appMap.get(appName);
      processApp(app);
    }
    log.info("Transform complete, we have " + eventList.size() + " events to post");

    return eventList;
  }

  private void processApp(Application app) {
    ArrayList<Metric> metricList = app.getMetricList();
    for (Metric metric : metricList) {
      processMetric(app, metric);
    }
  }

  private void processMetric(Application app, Metric metric) {
    // This is the timestamp of the latest event
    // only process timeslices more recent than this
    Date latest = latestMap.get(metric);
    // ArrayList<Timeslice> tsList = metric.getTimeslicesSince(latest);
    SortedMap<Date, Timeslice> tsMap = metric.getTimeslicesSince(latest);
    
    // Make an event per timeslice
    // for (Timeslice ts : tsList) {
    for (Date date : tsMap.keySet()) {
      Timeslice ts = tsMap.get(date);
      processTimeslice(app, metric, ts);
    }
  }

  private void processTimeslice(Application app, Metric metric, Timeslice ts) {
    Event e = new Event(config.getEventType());
    e.setTimestamp(ts.getTo());
    e.addStringAttribute("appName", app.getName());
    e.addStringAttribute("metricShort", metric.getShortName());
    e.addStringAttribute("metricFull", metric.getFullName());
    Map<String, Double> valueMap = ts.getValueMap();
    for (String key : valueMap.keySet()) {
      e.addNumericAttribute(key, valueMap.get(key));
    }
    eventList.add(e);
  }
}