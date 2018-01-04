package com.nrh.api.module.nr.dao;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Application {
  
  @Id
  private int id;

  private String name;
  private ArrayList<Metric> metricList;
  
  public Application(int id, String name) {
    this.id = id;
    this.name = name;
  }
  
  /**
   * @return the id
   */
  public int getId() {
    return id;
  }
  /**
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the metricList
   */
  public ArrayList<Metric> getMetricList() {
    return metricList;
  }
  /**
   * @param metricList the metricList to set
   */
  public void setMetricList(ArrayList<Metric> metricList) {
    this.metricList = metricList;
  }
}