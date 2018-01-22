package com.nrh.api.module.task.synthetics;

import org.json.JSONArray;
import org.json.JSONObject;

public class NrqlHelper {
  
  public static String makeFilterQuery(JSONArray jLocations) {
    // Start off the query
    String nrql = "SELECT " + makeFilter(jLocations.getString(0));

    // Add another section for each location
    for (int i=1; i < jLocations.length(); i++) {
      nrql += ", " + makeFilter(jLocations.getString(i));
    }
    nrql += " FROM SyntheticCheck FACET monitorName LIMIT 1000";
    return nrql;
  }

  private static String makeFilter(String locationLabel) {
    String snippet = "filter(latest(result), WHERE locationLabel='" + locationLabel +
      "'), filter(latest(duration), WHERE locationLabel='" + locationLabel + "')";
    return snippet;
  }
  
  public static JSONArray parseUniques(String sResponse) {
    JSONObject jResponse = new JSONObject(sResponse);
    JSONArray jResults = jResponse.getJSONArray("results");
    JSONObject jUniques = jResults.getJSONObject(0);
    JSONArray jLocations = jUniques.getJSONArray("members");
    jLocations.toList();
    return jLocations;
  }
}