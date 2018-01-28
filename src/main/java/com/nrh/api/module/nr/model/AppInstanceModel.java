package com.nrh.api.module.nr.model;

public class AppInstanceModel extends AppHostModel {
  private Integer instanceId;
  private Integer port;

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
}