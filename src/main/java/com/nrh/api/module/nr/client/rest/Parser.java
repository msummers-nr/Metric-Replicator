package com.nrh.api.module.nr.client.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nrh.api.module.nr.model.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Parser {
  
  private static final Logger log = LoggerFactory.getLogger(Parser.class);
  
  // Example date: 2018-01-02T19:30:00+00:00
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
  private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
  
  public static ArrayList<AppModel> strToAppList (String sResponse) {
    
    // Get the JSON format
    ArrayList<AppModel> appList = new ArrayList<>();
    JSONObject jResponse = new JSONObject(sResponse);
    JSONArray jAppList = jResponse.getJSONArray("applications");
    
    // Loop over the applications
    for (int i = 0; i < jAppList.length(); i++) {
      JSONObject jApp = jAppList.getJSONObject(i);
      AppModel app = jsonToAppModel(jApp);
      appList.add(app);
    }
    return appList;
  }

  public static AppModel strToAppModel (String sResponse) {
    JSONObject jResponse = new JSONObject(sResponse);
    JSONObject jApp = jResponse.getJSONObject("application");
    return jsonToAppModel(jApp);
  }

  public static ArrayList<MetricNameModel> strToMetricNames (Integer appId, String sResponse) {
    
    // Get the JSON format
    ArrayList <MetricNameModel> metricNameList = new ArrayList<>();
    JSONObject jResponse = new JSONObject(sResponse);
    JSONArray jMetricList = jResponse.getJSONArray("metrics");

    // Loop over the metrics
    for (int i = 0; i < jMetricList.length(); i++) {
      JSONObject jMetric = jMetricList.getJSONObject(i);
      String sName = jMetric.getString("name");
      MetricNameModel model = new MetricNameModel(appId, sName);
      metricNameList.add(model);
    }

    // Get the 
    return metricNameList;
  }

  public static ArrayList<MetricDataModel> strToMetricData (Integer appId, String sResponse) {

    // Get the JSON Format
    JSONObject jResponse = new JSONObject(sResponse);
    JSONObject jMetricData = jResponse.getJSONObject("metric_data");

    // Log errors for any not found metrics
    JSONArray jMetricsNotFound = jMetricData.getJSONArray("metrics_not_found");
    for (int i = 0; i < jMetricsNotFound.length(); i++) {
      String sMetric = jMetricsNotFound.getString(i);
      log.error("Metric not found: " + sMetric);
    }

    // Go through the array of metrics
    JSONArray jMetricArr = jMetricData.getJSONArray("metrics");
    return parseMetricArr(appId, jMetricArr);
  }

  private static AppModel jsonToAppModel(JSONObject jApp) {
    
    // Pull the string and int values straight from JSON
    AppModel app = new AppModel();
    app.setId(jApp.getInt("id"));
    app.setName(jApp.getString("name"));
    app.setLanguage(jApp.getString("language"));
    app.setHealthStatus(jApp.getString("health_status"));
    app.setReporting(jApp.getBoolean("reporting"));
    
    // Parse out the date
    try {
      if (jApp.has("last_reported_at")) {
        String sLastReportedAt = jApp.getString("last_reported_at");
        Date dLastReportedAt = df.parse(sLastReportedAt);
        app.setLastReportedAt(dLastReportedAt);
      }
    } catch (ParseException pe) {
      log.error(pe.getMessage(), pe);
    }
    return app;
  }

  private static ArrayList<MetricDataModel> parseMetricArr(Integer appId, JSONArray jMetricArr) {
    ArrayList<MetricDataModel> metricList = new ArrayList<>();

    // Loop through the array of metrics
    for (int i=0; i < jMetricArr.length(); i++) {
      // Create the metric
      JSONObject jMetric = jMetricArr.getJSONObject(i);
      String sName = jMetric.getString("name");
      MetricDataModel metric = new MetricDataModel(appId, sName);
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