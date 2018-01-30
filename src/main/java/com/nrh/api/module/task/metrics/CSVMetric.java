package com.nrh.api.module.task.metrics;

import com.opencsv.bean.CsvBindByName;

public class CSVMetric {

  @CsvBindByName
  private String appName;
  @CsvBindByName
  private Integer appId;
  @CsvBindByName
  private Boolean getHosts = false;
  @CsvBindByName
  private Boolean getInstances = false;
  @CsvBindByName
  private String metricName;
  @CsvBindByName
  private String shortName;

  /**
   * @return the appId
   */
  public Integer getAppId() {
    return appId;
  }
  /**
   * @param appId the appId to set
   */
  public void setAppId(Integer appId) {
    this.appId = appId;
  }
  /**
   * @return the appName
   */
  public String getAppName() {
    return appName;
  }
  /**
   * @param appName the appName to set
   */
  public void setAppName(String appName) {
    this.appName = appName;
  }
  /**
   * @return the metricName
   */
  public String getMetricName() {
    return metricName;
  }
  /**
   * @param metricName the metricName to set
   */
  public void setMetricName(String metricName) {
    this.metricName = metricName;
  }
  /**
   * @return the shortName
   */
  public String getShortName() {
    return shortName;
  }
  /**
   * @param shortName the shortName to set
   */
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }
}