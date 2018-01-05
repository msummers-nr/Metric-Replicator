package com.nrh.api.module.nr.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timeslice {
  
  private static final Logger log = LoggerFactory.getLogger(Timeslice.class);

  // Example date: 2018-01-02T19:30:00+00:00
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
  private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);

  private Date from;
  private Date to;
  private Map<String, Double> valueMap = new HashMap<String, Double>();

  /**
   * 
   */
  public void parseJSON(JSONObject jTimeslice) {
    parseDates(jTimeslice);
    JSONObject jValues = jTimeslice.getJSONObject("values");
    Iterator<String> iter = jValues.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      Double value = jValues.getDouble(key);
      valueMap.put(key, value);
    }
  }

  private void parseDates(JSONObject jTimeslice) {
    String sFrom = jTimeslice.getString("from");
    String sTo = jTimeslice.getString("to");

    try {
      from = df.parse(sFrom);
      to = df.parse(sTo);
    } catch(ParseException pe) {
      log.error(pe.getMessage(), pe);
    }
  }

  /**
   * @return the from
   */
  public Date getFrom() {
    return from;
  }

  /**
   * @return the to
   */
  public Date getTo() {
    return to;
  }

  /**
   * @return the values
   */
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