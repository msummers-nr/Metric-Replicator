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
import com.nrh.api.module.nr.Insights;
import com.nrh.api.module.nr.dao.*;

public class ExtractInsights {
  
  private static final Logger log = LoggerFactory.getLogger(ExtractInsights.class);
  
  private static final String NRQL = "SELECT latest(timestamp) FROM {eventType} FACET appName, metricFull LIMIT 1000";
  
  private CopierConfig copierConfig;
  private Insights destInsights;

  public ExtractInsights(CopierConfig copierConfig) {
    this.copierConfig = copierConfig;
    destInsights = new Insights(copierConfig.getDestKeys());
  }
  
  @Trace
  public Map<Metric, Date> queryInsights() throws IOException {
    
    // Use the correct eventType
    log.info("About to query Insights");
    String nrqlLive = NRQL.replace("{eventType}", copierConfig.getEventType());
    JSONArray jFacets = runQuery(nrqlLive);
    
    // Loop over each of the facets received
    Map<Metric, Date> latestMap = new HashMap<>();
    for (int i=0; i < jFacets.length(); i++) {
      JSONObject jFacet = jFacets.getJSONObject(i);
      processFacet(jFacet, latestMap);
    }
    
    log.info("Finished querying Insights");
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

  private void processFacet(JSONObject jFacet, Map<Metric, Date> latestMap) {
    
    // Get the app and metric names
    Metric metric = getMetricFromFacet(jFacet);
    long latest = getLatestFromFacet(jFacet);
    
    // Store in the latestMap
    Date date = new Date(latest);
    if (metric != null) {
      log.debug("* Insights: " + metric + " latest is " + date);
      latestMap.put(metric, date);
    }
  }

  private Metric getMetricFromFacet(JSONObject jFacet) {

    // Lookup the app
    JSONArray jName = jFacet.getJSONArray("name");

    // Make sure both facets came back correctly
    if (!jName.isNull(0) && !jName.isNull(1)) {

      // Lookup this app from our config
      String sAppName = jName.getString(0);
      Application app = copierConfig.getApplication(sAppName);
      String sMetricName = jName.getString(1);
      return getMetricFromApp(app, sMetricName);
    }

    return null;
  }

  private Metric getMetricFromApp(Application app, String sMetricName) {
    
    // The app may be null if the names don't match
    if (app != null) {
      Metric metric = app.getMetric(sMetricName);
      log.debug("* getMetric : " + metric + " from " + app);
      return metric;
    }
    return null;
  }

  private long getLatestFromFacet(JSONObject jFacet) {
    // Get the latest timestamp
    JSONArray jResults = jFacet.getJSONArray("results");
    JSONObject jLatest = jResults.getJSONObject(0);
    long latest = jLatest.getLong("latest");
    return latest;
  }
}
