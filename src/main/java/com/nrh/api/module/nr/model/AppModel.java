package com.nrh.api.module.nr.model;

import java.util.Date;

public class AppModel {
  private Integer appId;
  private String appName;
  private String language;
  private String healthStatus;
  private Boolean reporting;
  private Date lastReportedAt;

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

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getHealthStatus() {
    return healthStatus;
  }

  public void setHealthStatus(String healthStatus) {
    this.healthStatus = healthStatus;
  }

  public Boolean getReporting() {
    return reporting;
  }

  public void setReporting(Boolean reporting) {
    this.reporting = reporting;
  }

  public Date getLastReportedAt() {
    return lastReportedAt;
  }

  public void setLastReportedAt(Date lastReportedAt) {
    this.lastReportedAt = lastReportedAt;
  }
}