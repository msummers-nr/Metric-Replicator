package com.nrh.api.module.nr.model;

import com.nrh.api.module.nr.config.MetricConfig;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricDataModel extends MetricNameModel {
  private static final Logger log = LoggerFactory.getLogger(MetricDataModel.class);
  
  private TreeMap<Date, TimesliceModel> tsMap = new TreeMap<Date, TimesliceModel>();

  public MetricDataModel(MetricConfig metricConfig, String name) {
    super(metricConfig, name);
  }

  public SortedMap<Date, TimesliceModel> getTimeslicesSince(Date latest) {
    if (latest == null) {
      latest = new Date(0);
    }
    SortedMap<Date, TimesliceModel> tailMap = tsMap.tailMap(latest, false);
    log.debug("* " + getFullName() + " has " + tailMap.size() + " slices since " + latest);
    return tailMap;
  }

  public TreeMap<Date, TimesliceModel> getTsMap() {
    return tsMap;
  }

  public void setTsMap(TreeMap<Date, TimesliceModel> tsMap) {
    this.tsMap = tsMap;
  }

  public void addTimeslice(Date date, TimesliceModel ts) {
    tsMap.put(date, ts);
  }
}