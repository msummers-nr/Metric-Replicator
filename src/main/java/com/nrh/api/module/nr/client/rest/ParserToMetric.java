package com.nrh.api.module.nr.client.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ParserToMetric {
  
  private static final Logger log = LoggerFactory.getLogger(ParserToMetric.class);
  
  // Example date: 2018-01-02T19:30:00+00:00
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
  private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
  
  public static ArrayList<MetricNameModel> strToMetricNames (String sResponse, MetricConfig metricConfig) {
    
    // The metricType is "metrics" or "metric_data"
    String sJsonRoot = metricConfig.getMetricType();

    // Get the JSON format
    ArrayList <MetricNameModel> metricNameList = new ArrayList<>();
    JSONObject jResponse = new JSONObject(sResponse);
    JSONArray jMetricList = jResponse.getJSONArray(sJsonRoot);

    // Loop over the metrics
    for (int i = 0; i < jMetricList.length(); i++) {
      JSONObject jMetric = jMetricList.getJSONObject(i);
      String fullName = jMetric.getString("name");
      String shortName = metricConfig.getShortName(fullName);
      
      // Create the model (result)
      MetricNameModel model = new MetricNameModel(metricConfig, fullName, shortName);
      metricNameList.add(model);
    }

    // Get the 
    return metricNameList;
  }

  public static ArrayList<MetricDataModel> strToMetricData (String sResponse, MetricConfig metricConfig) {
    
    // The metricType is "metrics" or "metric_data"
    String sJsonRoot = metricConfig.getMetricType();
    
    // Get the JSON Format
    JSONObject jResponse = new JSONObject(sResponse);
    JSONObject jMetricData = jResponse.getJSONObject(sJsonRoot);

    // Log errors for any not found metrics
    JSONArray jMetricsNotFound = jMetricData.getJSONArray("metrics_not_found");
    for (int i = 0; i < jMetricsNotFound.length(); i++) {
      String sMetric = jMetricsNotFound.getString(i);
      log.error("Metric not found: " + sMetric);
    }

    // Go through the array of metrics
    JSONArray jMetricArr = jMetricData.getJSONArray("metrics");
    return parseMetricArr(metricConfig, jMetricArr);
  }

  private static ArrayList<MetricDataModel> parseMetricArr(MetricConfig metricConfig, JSONArray jMetricArr) {
    ArrayList<MetricDataModel> metricList = new ArrayList<>();

    // Loop through the array of metrics
    for (int i=0; i < jMetricArr.length(); i++) {
      
      // Get the full and short names
      JSONObject jMetric = jMetricArr.getJSONObject(i);
      String fullName = jMetric.getString("name");
      String shortName = metricConfig.getShortName(fullName);

      // Create the metric
      MetricDataModel metric = new MetricDataModel(metricConfig, fullName, shortName);
      metricList.add(metric);

      // Add timeslices to the metric
      JSONArray jTimeslices = jMetric.getJSONArray("timeslices");
      parseTimesliceArr(metric, jTimeslices);
    }

    return metricList;
  }

  private static void parseTimesliceArr(MetricDataModel metric, JSONArray jTimeslices) {
    // Loop through the array of timeslices
    for (int i=0; i < jTimeslices.length(); i++) {
      JSONObject jTimeslice = jTimeslices.getJSONObject(i);
      
      Date date = getTimesliceDate(jTimeslice);
      TimesliceModel ts = parseTimesliceJSON(jTimeslice);
      metric.addTimeslice(date, ts);
    }
  }

  private static Date getTimesliceDate(JSONObject jTimeslice) {
    Date dReturn = null;
    try {
      String sTo = jTimeslice.getString("to");
      dReturn = df.parse(sTo);
    } catch(ParseException pe) {
      log.error(pe.getMessage(), pe);
    }
    return dReturn;
  }

  private static TimesliceModel parseTimesliceJSON(JSONObject jTimeslice) {
    TimesliceModel ts = new TimesliceModel();
    JSONObject jValues = jTimeslice.getJSONObject("values");
    Iterator<String> iter = jValues.keys();
    while (iter.hasNext()) {
      String key = iter.next();
      Double value = jValues.getDouble(key);
      ts.addValue(key, value);
    }
    return ts;
  }

}