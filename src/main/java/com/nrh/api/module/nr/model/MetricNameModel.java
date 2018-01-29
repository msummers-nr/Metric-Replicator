package com.nrh.api.module.nr.model;

import com.nrh.api.module.nr.config.MetricConfig;

public class MetricNameModel {
  // protected MetricConfig metricConfig;
  
  private String appName;
  private Integer appId;
  private Integer hostId = 0;
  private Integer instanceId = 0;
  
  private String fullName;
  private String shortName;

  public MetricNameModel(MetricConfig metricConfig, String fullName) {
    // this.metricConfig = metricConfig;
    this.appId = metricConfig.getAppId();
    this.appName = metricConfig.getAppName();
    this.hostId = metricConfig.getHostId();
    this.instanceId = metricConfig.getInstanceId();
    this.fullName = fullName;
  }

  public MetricNameModel(Integer appId, Integer hostId, Integer instanceId, String fullName) {
    this.appId = appId;
    this.hostId = hostId;
    this.instanceId = instanceId;
    this.fullName = fullName;
  }

  public String getUniqueId() {
    return appId + "." + hostId + "." + instanceId + "." + fullName;
  }

  // public MetricConfig getMetricConfig() {
  //   return metricConfig;
  // }
  public String getAppName() {
    return appName;
  }
  public void setAppName(String appName) {
    this.appName = appName;
  }
  public Integer getAppId() {
    return appId;
  }
  public void setAppId(Integer appId) {
    this.appId = appId;
  }
  public Integer getHostId() {
    return hostId;
  }
  public void setHostId(Integer hostId) {
    this.hostId = hostId;
  }
  public Integer getInstanceId() {
    return instanceId;
  }
  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
  }
  public String getFullName() {
    return fullName;
  }
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  public String getShortName() {
    return shortName;
  }
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }
}