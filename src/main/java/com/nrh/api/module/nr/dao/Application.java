package com.nrh.api.module.nr.dao;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Application {
  
  @Id
  private int id;

  private String name;
  
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
}