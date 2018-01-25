package com.nrh.api.module.nr.model;

import java.util.Date;

public class AppModel extends BaseModel {
  private String language;
  private String healthStatus;
  private Boolean reporting;
  private Date lastReportedAt;

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