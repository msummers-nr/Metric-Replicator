package com.nrh.api.module.nr.task.synthetics;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nrh.api.module.nr.Plugins;

public class DataConverter {
  
  private JSONArray jResults;
  private int countSuccess = 0;
  private int countLocations = 0;
  private double dSumDuration = 0.0;
  private String monitorName;  
  private Map<String, Double> mPluginMetrics = new HashMap<String, Double>();
  private Map<String, Double> mInsightsMetrics = new HashMap<String, Double>();
  
  public DataConverter(JSONObject jMonitorData, JSONArray jLocations) {
    jResults = jMonitorData.getJSONArray("results");
    monitorName = jMonitorData.getString("name");
    
    // Process the data into the intermediary HashMaps
    processData(jLocations);
  }
  
  public String getMonitorName() {
    return monitorName;
  }
  
  public Map<String, Double> getPluginMap() {
    return mPluginMetrics;
  }
  
  public Map<String, Double> getInsightsMap() {
    return mInsightsMetrics;
  }
  
  private void processData(JSONArray jLocations) {
    // Loop over the location list
    for (int i=0; i < jLocations.length(); i++) {
      String sLocId = "Location/" + jLocations.getString(i);

      // Each monitor has 2 results per location (result and duration)
      int idxOffset = i * 2;
      
      // Only calculate the record if it's not null
      if (!jResults.getJSONObject(idxOffset).isNull("latest")) {
        calcMetrics(idxOffset, sLocId);        
      }
    }
    
    // After looping add the rollup metrics
    addRollupMetrics();
  }
  
  private void calcMetrics(int idxOffset, String sLocId) {
    countLocations++;
    String sLocResult = jResults.getJSONObject(idxOffset).getString("latest");
    double dLocDuration = jResults.getJSONObject(idxOffset + 1).getDouble("latest");
    dSumDuration += dLocDuration;
    
    // Add the metrics to the map
    boolean bSuccess = addLocationMetrics(sLocId, sLocResult, dLocDuration, mPluginMetrics);
    if (bSuccess) {
      countSuccess++;
    }
  }
  
  private boolean addLocationMetrics(String sLocId, String sLocResult, double dLocDuration, Map<String, Double> mMetrics) {
    
    // Build out the keys
    String sSuccessPct = Plugins.buildMetricName(sLocId, "Success", "pct");
    String sFailPct = Plugins.buildMetricName(sLocId, "Failure", "pct");
    String sDuration = Plugins.buildMetricName(sLocId, "Duration", "ms");
    
    // Store the values in the metric map
    mMetrics.put(sDuration, dLocDuration);
    if (sLocResult.equals("SUCCESS")) {
      mMetrics.put(sSuccessPct, 100.0);
      mMetrics.put(sFailPct, 0.0);
      return true;
    }
    mMetrics.put(sSuccessPct, 0.0);
    mMetrics.put(sFailPct, 100.0);
    return false;
  }
  
  private void addRollupMetrics() {
    double dSuccessRate = 100.0 * countSuccess / countLocations;
    double dAvgDuration = dSumDuration / countLocations;

    // Build out the keys
    String sRollupSuccessCnt = Plugins.buildMetricName("Overall", "Success", "count");
    String sRollupSuccesPct = Plugins.buildMetricName("Overall", "Success", "pct");
    String sRollupFailCnt = Plugins.buildMetricName("Overall", "Failure", "count");
    String sRollupFailPct = Plugins.buildMetricName("Overall", "Failure", "pct");
    String sRollupDuration = Plugins.buildMetricName("Overall", "Duration", "ms");

    // Store the values in the plugin map
    mPluginMetrics.put(sRollupSuccessCnt, new Double(countSuccess));
    mPluginMetrics.put(sRollupSuccesPct, dSuccessRate);
    mPluginMetrics.put(sRollupFailCnt, new Double(countLocations - countSuccess));
    mPluginMetrics.put(sRollupFailPct, 100 - dSuccessRate);
    mPluginMetrics.put(sRollupDuration, dAvgDuration);
    
    // Store the values in the insights map
    mInsightsMetrics.put("successCount", new Double(countSuccess));
    mInsightsMetrics.put("successRate", dSuccessRate);
    mInsightsMetrics.put("failCount", new Double(countLocations - countSuccess));
    mInsightsMetrics.put("failRate", 100 - dSuccessRate);
    mInsightsMetrics.put("duration", dAvgDuration);
    mInsightsMetrics.put("locationCount", new Double(countLocations));
  }
}
