package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.client.InsightsAPI;
import com.nrh.api.module.nr.model.MetricNameModel;

@Component
public class ExtractInsights {

	private static final Logger log = LoggerFactory.getLogger(ExtractInsights.class);

	private static final String NRQL = "SELECT latest(timestamp) FROM {eventType} FACET uniqueId SINCE 5 minutes ago LIMIT 1000";

	private String eventType;
	private InsightsAPI destInsights;
	@Autowired
	CopierConfig copierConfig;

	public ExtractInsights() {
	}

	@PostConstruct
	private void postConstruct() {
		this.eventType = copierConfig.getEventType();
		destInsights = new InsightsAPI(copierConfig.getDestKeys());
	}

	@Trace
	public Map<String, Date> queryInsights() throws IOException {
		log.debug("queryInsights: enter");
		String nrqlLive = NRQL.replace("{eventType}", eventType);
		JSONArray jFacets = runQuery(nrqlLive);

		Map<String, Date> latestMap = new HashMap<>();
		for (int i = 0; i < jFacets.length(); i++) {
			JSONObject jFacet = jFacets.getJSONObject(i);
			processFacet(jFacet, latestMap);
		}

		log.debug("queryInsights: exit:  latestMap.size: {}", latestMap.size());
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
		MetricNameModel metricNameModel = getMetricFromFacet(jFacet);
		long latest = getLatestFromFacet(jFacet);

		Date date = new Date(latest);
		if (metricNameModel != null) {
			log.debug("processFacet: metrciNameModel: {} date: {}", metricNameModel, date);
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
		Integer appId = safeParseInt(idSegments[0]);
		Integer hostId = safeParseInt(idSegments[1]);
		Integer instanceId = safeParseInt(idSegments[2]);
		String fullName = idSegments[3];
		// MetricConfig metricConfig = new MetricConfig(appId, hostId, instanceId);

		// Create the model with a link back to this config we just created
		// MetricNameModel metricNameModel = new MetricNameModel(metricConfig, metricName);
		MetricNameModel metricNameModel = new MetricNameModel(appId, hostId, instanceId, fullName);
		return metricNameModel;
	}

	private Integer safeParseInt(String idSegment) {
		try {
			return Integer.parseInt(idSegment);
		} catch (NumberFormatException nfe) {
			log.error("Trouble parsing uniqueId segment : " + idSegment + " defaulting to 0");
		}
		return 0;
	}

	private long getLatestFromFacet(JSONObject jFacet) {
		// Get the latest timestamp
		JSONArray jResults = jFacet.getJSONArray("results");
		JSONObject jLatest = jResults.getJSONObject(0);
		long latest = jLatest.getLong("latest");
		return latest;
	}
}
