package com.nrh.api.module.nr.task.synthetics;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nrh.api.module.nr.Plugins;

public class MonitorStats {
  
  private JSONArray jResults;
  private JSONArray jLocations;
  private int countSuccess = 0;
  private int countLocations = 0;
  private double dSumDuration = 0.0;
  
  public Map<String, Double> mMetrics = new HashMap<String, Double>();
  public String monitorName;
  
  public MonitorStats(JSONObject jMonitorData, JSONArray jLocations) {
    this.jLocations = jLocations;
    jResults = jMonitorData.getJSONArray("results");
    monitorName = jMonitorData.getString("name");
  }
  
  public void parse() {
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
    boolean bSuccess = addLocationMetrics(sLocId, sLocResult, dLocDuration, mMetrics);
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

    // Store the values in the metric map
    mMetrics.put(sRollupSuccessCnt, new Double(countSuccess));
    mMetrics.put(sRollupSuccesPct, dSuccessRate);
    mMetrics.put(sRollupFailCnt, new Double(countLocations - countSuccess));
    mMetrics.put(sRollupFailPct, 100 - dSuccessRate);
    mMetrics.put(sRollupDuration, dAvgDuration);
  }
}
