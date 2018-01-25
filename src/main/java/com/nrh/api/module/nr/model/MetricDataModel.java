package com.nrh.api.module.nr.model;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricDataModel extends BaseModel {
  private static final Logger log = LoggerFactory.getLogger(MetricDataModel.class);
  
  private Integer appId;
  private Integer instanceId;
  private String appName;
  private String shortName;
  private TreeMap<Date, TimesliceModel> tsMap = new TreeMap<Date, TimesliceModel>();

  public MetricDataModel(Integer appId, String name) {
    this.appId = appId;
    this.instanceId = 0;
    this.name = name;
  }
  public MetricDataModel(Integer appId, Integer instanceId, String name) {
    this.appId = appId;
    this.instanceId = instanceId;
    this.name = name;
  }
  
  public String getUniqueId() {
    if (instanceId != null) {
      return appId + "." + instanceId + "." + name;
    }
    return appId + ".0." + name;
  }

  public SortedMap<Date, TimesliceModel> getTimeslicesSince(Date latest) {
    if (latest == null) {
      latest = new Date(0);
    }
    SortedMap<Date, TimesliceModel> tailMap = tsMap.tailMap(latest, false);
    log.debug("* " + name + " has " + tailMap.size() + " slices since " + latest);
    return tailMap;
  }
  
  public Integer getAppId() {
    return appId;
  }

  public Integer getInstanceId() {
    return instanceId;
  }

  public String getAppName() {
    return appName;
  }
  
  public void setAppName(String appName) {
    this.appName = appName;
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