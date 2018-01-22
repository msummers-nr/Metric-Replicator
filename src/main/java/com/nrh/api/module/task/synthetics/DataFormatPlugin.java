package com.nrh.api.module.task.synthetics;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataFormatPlugin {
  
  private JSONObject jAgent;
  private JSONArray jComponents;
  
  public DataFormatPlugin() {
    // Initialize the agent section
    jAgent = new JSONObject();
    jAgent.put("host", RuntimeHelper.getHostName());
    jAgent.put("pid", RuntimeHelper.getPid());
    jAgent.put("version", SyntheticsCopier.VERSION);
    
    // Initialize the components[]
    jComponents = new JSONArray();
  }
  
  public JSONObject getJSON() {
    JSONObject jMessage = new JSONObject();
    jMessage.put("agent", jAgent);
    jMessage.put("components", jComponents);
    return jMessage;
  }
  
  public void addMonitorData(DataConverter converter) {
    
    // Build out the component section for this monitor and metric data
    JSONObject jCmp = new JSONObject();
    jCmp.put("name", converter.getMonitorName());
    jCmp.put("guid", SyntheticsCopier.GUID);
    jCmp.put("duration", 30);
    jCmp.put("metrics", converter.getPluginMap());
    jComponents.put(jCmp);
  }
}
