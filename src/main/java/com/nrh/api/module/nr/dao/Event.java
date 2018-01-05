package com.nrh.api.module.nr.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class Event {
  private Date timestamp;
  private String eventType;
  private Map<String,String> sAttributes = new HashMap<String, String>();
  private Map<String,Double> nAttributes = new HashMap<String, Double>();

  public Event(String eventType) {
    this.eventType = eventType;
  }

  /**
   * @return the timestamp
   */
  public Date getTimestamp() {
    return timestamp;
  }
  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
  /**
   * @return the eventType
   */
  public String getEventType() {
    return eventType;
  }
  /**
   * @param eventType the eventType to set
   */
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public void addStringAttribute(String name, String value) {
    sAttributes.put(name, value);
  }

  public void addNumericAttribute(String name, Double value) {
    nAttributes.put(name, value);
  }

  public JSONObject toJSON() {
    JSONObject jEvent = new JSONObject();
    if (timestamp != null) { jEvent.put("timestamp", timestamp.getTime() ); }
    jEvent.put("eventType", eventType);
    
    // Add all the string attributes
    for (String name : sAttributes.keySet()) {
      jEvent.put(name, sAttributes.get(name));
    }

    // Add all the numeric attributes
    for (String name : nAttributes.keySet()) {
      jEvent.put(name, nAttributes.get(name));
    }
    
    return jEvent;
  }
}