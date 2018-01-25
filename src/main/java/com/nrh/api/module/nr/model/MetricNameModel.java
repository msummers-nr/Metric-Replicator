package com.nrh.api.module.nr.model;

public class MetricNameModel extends BaseModel {
  private Integer appId;
  private Integer instanceId;
  private String shortName;

  public MetricNameModel(Integer appId, String name) {
    this.appId = appId;
    this.instanceId = 0;
    this.name = name;
  }
  
  public MetricNameModel(Integer appId, Integer instanceId, String name) {
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
  public Integer getAppId() {
    return appId;
  }
  public void setAppId(Integer appId) {
    this.appId = appId;
  }
  public Integer getInstanceId() {
    return instanceId;
  }
  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
  }
  public String getShortName() {
    return shortName;
  }
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }
}