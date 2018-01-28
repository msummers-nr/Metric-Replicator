package com.nrh.api.module.nr.model;

public class AppHostModel extends AppModel {
  private Integer hostId;
  private String host;

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
}