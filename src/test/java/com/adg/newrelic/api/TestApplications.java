package com.adg.newrelic.api;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import okhttp3.Response;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestApplications {

	private static final Logger log = LoggerFactory.getLogger(TestApplications.class);
	
	private static final String METRIC_NAMES = "Agent/MetricsReported/count";

	private APIKeyset keys;
	private Applications applications; 
	private static int appId;

	@Before
	public void setUp() throws Exception {
		
		// Read in the config files
		Config conf = ConfigFactory.load();
		
		// Get the first config from the array
		List<String> configArr = conf.getStringList("newrelic-api-lib.configArr");
		String configId = "newrelic-api-lib." + configArr.get(0);
		
		// Create the API Keyset for this specific configId
		keys = new APIKeyset(configId);
		log.info("Application API Test using keyset for account: " + keys.getAccountId());
		
		// Initialize the Applications API
		applications = new Applications(keys);
	}

	@Test
	public void test1ListSync() throws IOException {
		String sResponse = applications.listSync();
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray jApplications = jResponse.getJSONArray("applications");
		log.info("Number of applications: " + jApplications.length());
		
		// There should be more than 0 applications
		assertNotEquals(0, jApplications.length());
		
		// Store the application id from the first in the array
		appId = jApplications.getJSONObject(0).getInt("id");
		log.info("Setting appId to: " + appId);
	}
	
	@Test
	public void test2ShowSync() throws IOException {
		// log.info("test2ShowSync(" + appId + ")");
		// appId = 43192210;
		String sResponse = applications.showSync(appId);
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(sResponse);
		int returnedAppId = jResponse.getJSONObject("application").getInt("id");
		assertEquals(appId, returnedAppId);
	}

	@Test
	public void test3MetricNamesSync() throws IOException {
		String sResponse = applications.metricNamesSync(appId, null);
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray jMetrics = jResponse.getJSONArray("metrics");
		log.info("Number of metrics: " + jMetrics.length());

		// There should be more than 0 metrics
		assertNotEquals(0, jMetrics.length());
	}

	@Test
	public void test4MetricDataSync() throws IOException {
		String sResponse = applications.metricDataSync(appId, METRIC_NAMES);
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray metricsNotFound = jResponse.getJSONObject("metric_data").getJSONArray("metrics_not_found");
		log.info("Count of metrics not found: " + metricsNotFound.length());

		// There should be 0 metrics not found
		assertEquals(0, metricsNotFound.length());
	}
}
