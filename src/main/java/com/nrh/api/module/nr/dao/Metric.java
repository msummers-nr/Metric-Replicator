package com.nrh.api.module.nr.dao;

import java.util.Date;
import java.util.Map;

public class Metric {
  private String shortName;
  private String fullName;
  private String metricValue;
  private Map<Date, Float> timeslices;

  public Metric(String fullName) {
    this.fullName = fullName;
  }
  /**
   * @return the fullName
   */
  public String getFullName() {
    return fullName;
  }
  /**
   * @param fullName the fullName to set
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
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
  /**
   * @return the metricValue
   */
  public String getMetricValue() {
    return metricValue;
  }
  /**
   * @param metricValue the metricValue to set
   */
  public void setMetricValue(String metricValue) {
    this.metricValue = metricValue;
  }
  /**
   * @param timeslices the timeslices to set
   */
  public void setTimeslices(Map<Date, Float> timeslices) {
    this.timeslices = timeslices;
  }

  /**
   * @return the timeslices
   */
  public Map<Date, Float> getTimeslices() {
    return timeslices;
  }
}