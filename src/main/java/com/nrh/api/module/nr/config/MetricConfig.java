package com.nrh.api.module.nr.config;

import java.util.ArrayList;
// import java.util.Date;

public class MetricConfig extends AppConfig {
  public static final String TYPE_METRIC_NAME = "metrics";
  public static final String TYPE_METRIC_DATA = "metric_data";

  private String metricType;
  private String filterName;
  private ArrayList<String> metricNameList = new ArrayList<>();

  public MetricConfig(Integer appId, String appName) {
    this.appId = appId;
    this.appName = appName;
  }

  public MetricConfig(Integer appId, Integer hostId, Integer instanceId) {
    this.appId = appId;
    this.hostId = hostId;
    this.instanceId = instanceId;
  }

  public void addMetricName(String metricName) {
    metricNameList.add(metricName);
  }

  public String getMetricType() {
    return metricType;
  }
  
  public void setMetricType(String metricType) {
    this.metricType = metricType;
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