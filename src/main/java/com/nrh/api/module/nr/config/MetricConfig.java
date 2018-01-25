package com.nrh.api.module.nr.config;

import java.util.ArrayList;
// import java.util.Date;

public class MetricConfig {
  
  private Integer appId;
  private Integer instanceId;
  private String appName;

  private String filterName;
  private ArrayList<String> metricNameList = new ArrayList<>();
  // private ArrayList<String> metricValueList;
  // private Date from;
  // private Date to;
  // private Integer period;
  // private Boolean summarize;
  // private Boolean raw;

  public MetricConfig(Integer appId, String appName) {
    this.appId = appId;
    this.appName = appName;
  }

  public MetricConfig(Integer appId, Integer instanceId, String appName) {
    this.appId = appId;
    this.instanceId = instanceId;
    this.appName = appName;
  }

  public void addMetricName(String metricName) {
    metricNameList.add(metricName);
  }

  public String getUniqueId() {
    if (instanceId != null) {
      return appId + "." + instanceId;
    }
    return appId + ".0.";
  }

  public Integer getAppId() {
    return appId;
  }

  public void setAppId(Integer appId) {
    this.appId = appId;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }
  
  public Integer getInstanceId() {
    return instanceId;
  }
  
  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
  }

  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }
  
  public String getFilterName() {
    return filterName;
  }
  public ArrayList<String> getMetricNameList() {
    return metricNameList;
  }

  public void setMetricNameList(ArrayList<String> metricNameList) {
    this.metricNameList = metricNameList;
  }

}