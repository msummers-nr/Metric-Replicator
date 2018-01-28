package com.nrh.api.module.nr.model;

import com.nrh.api.module.nr.config.MetricConfig;

public class MetricNameModel {
  protected MetricConfig metricConfig;
  
  private String fullName;
  private String shortName;

  public MetricNameModel(MetricConfig metricConfig, String fullName) {
    this.metricConfig = metricConfig;
    this.fullName = fullName;
  }

  public String getUniqueId() {
    return metricConfig.getUniqueId() + "." + fullName;
  }

  public MetricConfig getMetricConfig() {
    return metricConfig;
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