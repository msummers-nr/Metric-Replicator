package com.nrh.api.module.nr.config;

import java.util.HashMap;
import java.util.Map;

public class AppListConfig {
  private String appId;
  private boolean sortHealthStatus;
  private Map<String, String> filterMap = new HashMap<>();

  public String getAppId() {
    return appId;
  }
  public void setAppId(String appId) {
    this.appId = appId;
  }
  public boolean getSortHealthStatus() {
    return sortHealthStatus;
  }
  public void setSortHealthStatus(boolean sortHealthStatus) {
    this.sortHealthStatus = sortHealthStatus;
  }
  public Map<String, String> getFilterMap() {
    return filterMap;
  }
  public void setFilterMap(Map<String, String> filterMap) {
    this.filterMap = filterMap;
  }
}