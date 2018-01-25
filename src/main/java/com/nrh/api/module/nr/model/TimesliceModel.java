package com.nrh.api.module.nr.model;

import java.util.HashMap;
import java.util.Map;

public class TimesliceModel extends BaseModel {
  
  private Map<String, Double> valueMap = new HashMap<String, Double>();

  public Map<String, Double> getValueMap() {
    return valueMap;
  }

  public void addValue(String key, Double value) {
    valueMap.put(key, value);
  }

  public Double getValue(String key) {
    return valueMap.get(key);
  }
}