package com.nrh.api.module.nr.model;

import com.nrh.api.module.nr.config.MetricConfig;

public class MetricNameModel extends BaseModel {
  private MetricConfig cfg;
  private String shortName;

  public MetricNameModel(MetricConfig cfg, String name) {
    this.cfg = cfg;
    this.name = name;
  }
  public String getUniqueId() {
    return cfg.getUniqueId() + "." + name;
  }
  public String getShortName() {
    return shortName;
  }
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }
}