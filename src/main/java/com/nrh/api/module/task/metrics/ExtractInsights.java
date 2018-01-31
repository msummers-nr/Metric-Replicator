package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.client.InsightsAPI;
import com.nrh.api.module.nr.model.MetricNameModel;

public class ExtractInsights {
  
  private static final Logger log = LoggerFactory.getLogger(ExtractInsights.class);
  
  private static final String NRQL = "SELECT latest(timestamp) FROM {eventType} FACET uniqueId LIMIT 1000";
  
  private String eventType;
  private InsightsAPI destInsights;

  public ExtractInsights(CopierConfig copierConfig) {
    this.eventType = copierConfig.getEventType();
    destInsights = new InsightsAPI(copierConfig.getDestKeys());
  }
  
  @Trace
  public Map<String, Date> queryInsights() throws IOException {
    
    // Use the correct eventType
    log.info("About to query Insights");
    String nrqlLive = NRQL.replace("{eventType}", eventType);
    JSONArray jFacets = runQuery(nrqlLive);
    
    // Loop over each of the facets received
    Map<String, Date> latestMap = new HashMap<>();
    for (int i=0; i < jFacets.length(); i++) {
      JSONObject jFacet = jFacets.getJSONObject(i);
      processFacet(jFacet, latestMap);
    }
    
    log.info("Finished querying Insights, latest maps has " + latestMap.size() + " dates.");
    return latestMap;
  }

  private JSONArray runQuery(String nrqlLive) throws IOException {
    
    // Query Insights
    // log.info(nrqlLive);
    String sResponse = destInsights.querySync(nrqlLive);
    JSONObject jResponse = new JSONObject(sResponse);
    JSONArray jFacets = jResponse.getJSONArray("facets");
    log.info("Received " + jFacets.length() + " facets from Insights");
    return jFacets;
  }

  private void processFacet(JSONObject jFacet, Map<String, Date> latestMap) throws IOException {
    
    // Get the app and metric names
    MetricNameModel metricNameModel = getMetricFromFacet(jFacet);
    long latest = getLatestFromFacet(jFacet);
    
    // Store in the latestMap
    Date date = new Date(latest);
    if (metricNameModel != null) {
      log.debug("* Insights: " + metricNameModel + " latest is " + date);
      String uniqueId = metricNameModel.getUniqueId();
      latestMap.put(uniqueId, date);
    }
  }

  private MetricNameModel getMetricFromFacet(JSONObject jFacet) throws IOException {

    String uniqueId = jFacet.getString("name");

    // uniqueId format: {appId}.{hostId}.{instanceId}.{metricName}
    String[] idSegments = uniqueId.split("\\.", 4);
    if (idSegments.length != 4) {
      throw new IOException("uniqueId does not have 4 segments");
    }
    
    // Grab the values from each segment to re-create the MetricConfig
    Integer appId = Integer.parseInt(idSegments[0]);
    Integer hostId = Integer.parseInt(idSegments[1]);
    Integer instanceId = Integer.parseInt(idSegments[2]);
    String fullName = idSegments[3];
    // MetricConfig metricConfig = new MetricConfig(appId, hostId, instanceId);
    
    // Create the model with a link back to this config we just created
    // MetricNameModel metricNameModel = new MetricNameModel(metricConfig, metricName);
    MetricNameModel metricNameModel = new MetricNameModel(appId, hostId, instanceId, fullName);
    return metricNameModel;
  }

  private long getLatestFromFacet(JSONObject jFacet) {
    // Get the latest timestamp
    JSONArray jResults = jFacet.getJSONArray("results");
    JSONObject jLatest = jResults.getJSONObject(0);
    long latest = jLatest.getLong("latest");
    return latest;
  }
}
