package com.nrh.api.module.nr.model;

import com.nrh.api.module.nr.config.MetricConfig;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricDataModel extends BaseModel {
  private static final Logger log = LoggerFactory.getLogger(MetricDataModel.class);
  
  private MetricConfig metricConfig;
  private String shortName;
  private TreeMap<Date, TimesliceModel> tsMap = new TreeMap<Date, TimesliceModel>();

  public MetricDataModel(MetricConfig metricConfig, String name) {
    this.metricConfig = metricConfig;
    this.name = name;
  }
  
  public String getUniqueId() {
    return metricConfig.getUniqueId() + "." + name;
  }
  
  public MetricConfig getMetricConfig() {
    return metricConfig;
  }
  
  public void setMetricConfig(MetricConfig metricConfig) {
    this.metricConfig = metricConfig;
  }

  public SortedMap<Date, TimesliceModel> getTimeslicesSince(Date latest) {
    if (latest == null) {
      latest = new Date(0);
    }
    SortedMap<Date, TimesliceModel> tailMap = tsMap.tailMap(latest, false);
    log.debug("* " + name + " has " + tailMap.size() + " slices since " + latest);
    return tailMap;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
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