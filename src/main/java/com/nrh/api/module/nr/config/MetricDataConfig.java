package com.nrh.api.module.nr.config;

import java.util.ArrayList;
// import java.util.Date;

public class MetricDataConfig {
  private Integer appId;
  private String appName;
  private ArrayList<String> metricNameList;
  // private ArrayList<String> metricValueList;
  // private Date from;
  // private Date to;
  // private Integer period;
  // private Boolean summarize;
  // private Boolean raw;

  public MetricDataConfig(Integer appId, String appName) {
    this.appId = appId;
    metricNameList = new ArrayList<>();
  }

  public void addMetricName(String metricName) {
    metricNameList.add(metricName);
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

  public ArrayList<String> getMetricNameList() {
    return metricNameList;
  }

  public void setMetricNameList(ArrayList<String> metricNameList) {
    this.metricNameList = metricNameList;
  }

}