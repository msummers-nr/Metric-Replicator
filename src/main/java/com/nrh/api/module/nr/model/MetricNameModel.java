package com.nrh.api.module.nr.model;

import com.nrh.api.module.nr.config.MetricConfig;

public class MetricNameModel extends BaseModel {
  private MetricConfig metricConfig;
  private String shortName;

  public MetricNameModel(MetricConfig metricConfig, String name) {
    this.metricConfig = metricConfig;
    this.name = name;
  }
  public String getUniqueId() {
    return metricConfig.getUniqueId() + "." + name;
  }
  public String getShortName() {
    return shortName;
  }
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }
}