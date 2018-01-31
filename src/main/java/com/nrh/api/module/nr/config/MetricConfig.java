package com.nrh.api.module.nr.config;

import java.util.Collection;
import java.util.HashMap;

public class MetricConfig extends AppConfig {
  public static final String TYPE_METRIC_NAME = "metrics";
  public static final String TYPE_METRIC_DATA = "metric_data";

  private String metricType;
  private String filterName;
  private HashMap<String, String> metricNameMap = new HashMap<>();

  public void addMetricName(String fullName, String shortName) {
    metricNameMap.put(fullName, shortName);
  }

  public String getMetricType() {
    return metricType;
  }
  
  public void setMetricType(String metricType) {
    this.metricType = metricType;
  }

  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }
  
  public String getFilterName() {
    return filterName;
  }
  
  public Collection<String> getMetricNameList() {
    return metricNameMap.keySet();
  }

  public String getShortName(String fullName) {
    return metricNameMap.get(fullName);
  }
}