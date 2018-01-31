package com.nrh.api.module.nr.config;

import java.util.HashMap;
import java.util.Map;

public class AppConfig implements Cloneable {

  public static final String TYPE_APP_ONLY = "application";
  public static final String TYPE_APP_HOST = "application_host";
  public static final String TYPE_APP_INSTANCE = "application_instance";

  private Integer appId = 0;
  private String appName;
  private Integer hostId = 0;
  private String host;
  private Integer instanceId = 0;
  private Integer port;
  private String configType;
  private boolean sortHealthStatus;
  private Map<String, String> filterMap = new HashMap<>();

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
  public String getHost() {
    return host;
  }
  public void setHost(String host) {
    this.host = host;
  }
  public Integer getInstanceId() {
    return instanceId;
  }
  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
  }
  public Integer getPort() {
    return port;
  }
  public void setPort(Integer port) {
    this.port = port;
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
  public Object clone() throws CloneNotSupportedException{  
    return super.clone();  
  } 
}