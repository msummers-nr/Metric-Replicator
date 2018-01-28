package com.nrh.api.module.nr.config;

import java.util.ArrayList;
// import java.util.Date;

public class MetricConfig {
  public static final String TYPE_METRIC_NAME = "metrics";
  public static final String TYPE_METRIC_DATA = "metric_data";

  private String metricType;
  private ApplicationConfig appConfig;
  // private Integer appId;
  // private Integer hostId;
  // private Integer instanceId;
  // private String appName;

  private String filterName;
  private ArrayList<String> metricNameList = new ArrayList<>();
  // private ArrayList<String> metricValueList;
  // private Date from;
  // private Date to;
  // private Integer period;
  // private Boolean summarize;
  // private Boolean raw;

  public MetricConfig(ApplicationConfig appConfig) {
    this.appConfig = appConfig;
  }

  public MetricConfig(Integer appId, String appName) {
    // Create the appConfig if it doesn't exist
    ApplicationConfig appConfig = new ApplicationConfig(ApplicationConfig.TYPE_APP_ONLY);
    appConfig.setAppId(appId);
    appConfig.setAppName(appName);
    this.appConfig = appConfig;
  }

  public void addMetricName(String metricName) {
    metricNameList.add(metricName);
  }

  public String getUniqueId() {
    return appConfig.getUniqueId();
  }

  public String getMetricType() {
    return metricType;
  }
  
  public void setMetricType(String metricType) {
    this.metricType = metricType;
  }

  public Integer getAppId() {
    return appConfig.getAppId();
  }

  // public void setAppId(Integer appId) {
  //   this.appId = appId;
  // }

  public String getAppName() {
    return appConfig.getAppName();
  }

  // public void setAppName(String appName) {
  //   this.appName = appName;
  // }
  
  public Integer getInstanceId() {
    return appConfig.getAppId();
  }
  
  public void setInstanceId(Integer instanceId) {
    this.appConfig.setInstanceId(instanceId);;
  }
  
  public Integer getHostId() {
    return appConfig.getHostId();
  }
  
  // public void setHostId(Integer hostId) {
  //   this.hostId = hostId;
  // }

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