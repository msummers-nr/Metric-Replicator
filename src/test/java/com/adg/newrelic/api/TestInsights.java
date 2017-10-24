package com.adg.newrelic.api;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestInsights {

	private static final Logger log = LoggerFactory.getLogger(TestInsights.class);
	
	// API keys we'll use for the tests
	private APIKeyset keys;
	private Insights insights;
	
	public static final String NRQL_QUERY = "SELECT count(*) FROM Transaction";
	public static final long TIMEOUT = 10000;
	
	@Before
	public void setUp() throws Exception {
		
		// Read in the config files
		Config conf = ConfigFactory.load();
		log.info("Reading config file: " + conf.origin());
		
		// Get the name of the unitTestAccount
		String unitTestAccount = conf.getString("newrelic-api-client.tests.unitTestAccount");
		keys = new APIKeyset(conf, unitTestAccount);
		log.info("Insights API Test using keyset for account: " + keys.getAccountName());
		
		// Initialize the Insights API
		insights = new Insights(keys);
	}

	@Test
	public void testQuerySync() throws IOException {
		String sResponse = insights.querySync(NRQL_QUERY);
		
		// Convert the response into JSON and pull out the count
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray jResults = jResponse.getJSONArray("results");
		Long lCount = jResults.getJSONObject(0).getLong("count");
		assertNotNull(lCount);
		log.info("[Sync] count is: " + lCount.toString());
	}

	@Test
	public void testInsertSync() throws IOException {
		
		// Create an array with a couple of events
		JSONArray jEvents = new JSONArray();
		jEvents.put(new JSONObject()
			.put("eventType", "ZZZTest")
			.put("testInt", 100)
			.put("testString", "Test100Value")
		);
		jEvents.put(new JSONObject()
			.put("eventType", "ZZZTest")
			.put("testInt", 50)
			.put("testString", "Test50Value")
		);
		
		// Insert those events via API call
		String sResponse = insights.insertSync(jEvents);

		// Convert the response into JSON
		JSONObject jResponse = new JSONObject(sResponse);
		boolean bSuccess = jResponse.getBoolean("success");
		assertTrue(bSuccess);
	}
}