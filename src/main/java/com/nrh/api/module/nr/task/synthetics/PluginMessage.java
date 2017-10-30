package com.nrh.api.module.nr.task.synthetics;

import org.json.JSONArray;
import org.json.JSONObject;

public class PluginMessage {
  
  private JSONObject jAgent;
  private JSONArray jComponents;
  
  public PluginMessage() {
    // Initialize the agent section
    jAgent = new JSONObject();
    jAgent.put("host", Runtime.getHostName());
    jAgent.put("pid", Runtime.getPid());
    jAgent.put("version", Copier.VERSION);
    
    // Initialize the components[]
    jComponents = new JSONArray();
  }
  
  public JSONObject getMessage() {
    JSONObject jMessage = new JSONObject();
    jMessage.put("agent", jAgent);
    jMessage.put("components", jComponents);
    return jMessage;
  }
  
//  public void addMetrics(String monitorName, Map<String, Double> mMetrics) {
  public void addMetrics(MonitorStats mStats) {
    
    // Build out the component section for this monitor and metric data
    JSONObject jCmp = new JSONObject();
    jCmp.put("name", mStats.monitorName);
    jCmp.put("guid", Copier.GUID);
    jCmp.put("duration", 30);
    jCmp.put("metrics", mStats.mMetrics);
    jComponents.put(jCmp);
  }
}
