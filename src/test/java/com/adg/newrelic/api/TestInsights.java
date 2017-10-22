package com.adg.newrelic.api;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TestInsights {

	private static final Logger log = LoggerFactory.getLogger(TestInsights.class);

	// Used to ensure the async work has completed
	private CountDownLatch lock = new CountDownLatch(1);
	private Long lCountAsync;
	
	// API keys we'll use for the tests
	private APIKeyset keys;
	private Insights insights;
	
	public static final String NRQL_QUERY = "SELECT count(*) FROM Transaction";
	public static final long TIMEOUT = 10000;
	
	@Before
	public void setUp() throws Exception {
		
		// Read in the config files
		Config conf = ConfigFactory.load();
		
		// Get the first config from the array
		List<String> configArr = conf.getStringList("newrelic-api-lib.configArr");
		String configId = "newrelic-api-lib." + configArr.get(0);
		
		// Create the API Keyset for this specific configId
		keys = new APIKeyset(configId);
		log.info("Insights API Test using keyset for account: " + keys.getAccountId());
		
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

	// @Test
	// public void testQueryAsync() throws IOException, InterruptedException {
		
	// 	// Call the async version of the API
	// 	insights.queryAsync(NRQL_QUERY, new Callback() {

	// 		@Override
	// 		public void onFailure(Call call, IOException e) {
	// 			assertFalse(e.getMessage(), true);
	// 		}

	// 		@Override
	// 		public void onResponse(Call call, Response rsp) throws IOException {
				
	// 			// Convert the response into JSON and pull out the count
	// 			JSONObject jResponse = new JSONObject(rsp.body().string());
	// 			JSONArray jResults = jResponse.getJSONArray("results");
	// 			lCountAsync = jResults.getJSONObject(0).getLong("count");
	// 			assertNotNull(lCountAsync);
	// 			log.info("[Async] count is: " + lCountAsync.toString());
				
	// 			// Tell the lock this value has been returned
	// 			lock.countDown();
	// 		}
	// 	});
		
	// 	// Wait for the lock to count down from the callback
	// 	lock.await(TIMEOUT, TimeUnit.MILLISECONDS);
	// 	assertNotNull(lCountAsync);
		
	// }

}