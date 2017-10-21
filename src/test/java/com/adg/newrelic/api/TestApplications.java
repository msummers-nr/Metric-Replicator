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

import okhttp3.Response;

public class TestApplications {

	private static final Logger log = LoggerFactory.getLogger(TestApplications.class);
			
	// API keys we'll use for the tests
	private APIKeyset keys;

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
	}

	@Test
	public void testListSync() throws IOException {
		Response rsp = Applications.listSync(keys);
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(rsp.body().string());
		JSONArray jApplications = jResponse.getJSONArray("applications");
		log.info("Number of applications: " + jApplications.length());
		
		// There should be more than 0 applications
		assertNotEquals(jApplications.length(), 0);
		
	}
}
