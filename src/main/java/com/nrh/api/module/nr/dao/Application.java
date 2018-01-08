package com.nrh.api.module.nr.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Application {
  
  @Id
  private int id;

  private String name;
  private ArrayList<Metric> metricList = new ArrayList<Metric>();
  
  public Application(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public void setMetricList(List<Metric> metricList) {
    for (Metric metric : metricList) {
      String fullName = metric.getFullName();
      String shortName = metric.getFullName();

      addNewMetric(fullName, shortName);
    }
  }

  public void addNewMetric(String fullName, String shortName) {
    Metric metric = new Metric(fullName, shortName);
    metricList.add(metric);
  }

  public Metric getMetric(String fullName) {
    for (Metric metric : metricList) {
      if (fullName.equals(metric.getFullName())) {
        return metric;
      }
    }
    return null;
  }

  public String toString() {
    return name + " (" + id + ")";
  }
  
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public ArrayList<Metric> getMetricList() {
    return metricList;
  }
}