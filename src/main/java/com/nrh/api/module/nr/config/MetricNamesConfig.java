package com.nrh.api.module.nr.config;

public class MetricNamesConfig {
  private Integer appId;
  private String filterName;

  public MetricNamesConfig(Integer appId) {
    this.appId = appId;
  }
  
  public Integer getAppId() {
    return appId;
  }

  public void setAppId(Integer appId) {
    this.appId = appId;
  }

  public String getFilterName() {
    return filterName;
  }

  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }
}