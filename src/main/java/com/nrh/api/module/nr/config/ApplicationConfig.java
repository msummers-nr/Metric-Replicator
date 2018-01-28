package com.nrh.api.module.nr.config;

import java.util.HashMap;
import java.util.Map;

public class ApplicationConfig {

  public static final String TYPE_APP_ONLY = "application";
  public static final String TYPE_APP_HOST = "application_host";
  public static final String TYPE_APP_INSTANCE = "application_instance";

  private String configType;
  private Integer appId;
  private String appName;
  private Integer hostId;
  private Integer instanceId;
  private boolean sortHealthStatus;
  private Map<String, String> filterMap = new HashMap<>();

  public ApplicationConfig(String configType) {
    this.configType = configType;
  }

  public String getUniqueId() {
    if (instanceId != null) {
      return appId + "." + instanceId;
    }
    if (hostId != null) {
      return appId + "." + hostId;
    }
    return appId + ".0.";
  }

  public String getConfigType() {
    return configType;
  }
  public void setConfigType(String configType) {
    this.configType = configType;
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