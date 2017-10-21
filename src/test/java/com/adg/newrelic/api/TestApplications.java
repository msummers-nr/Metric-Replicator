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
		Response rsp = applications.listSync();
		
		// Convert the response into JSON and count the number of applications
		String sResponse = rsp.body().string();
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray jApplications = jResponse.getJSONArray("applications");
		log.info("Number of applications: " + jApplications.length());
		
		// There should be more than 0 applications
		assertNotEquals(jApplications.length(), 0);
		
		// Store the application id from the first in the array
		appId = jApplications.getJSONObject(0).getInt("id");
		log.info("Setting appId to: " + appId);
	}
	

	@Test
	public void test2ShowSync() throws IOException {
		log.info("test2ShowSync(" + appId + ")");
		Response rsp = applications.showSync(appId);
		
		// Convert the response into JSON and count the number of applications
		String sResponse = rsp.body().string();
		JSONObject jResponse = new JSONObject(sResponse);
		int returnedAppId = jResponse.getJSONObject("application").getInt("id");
		assertEquals(appId, returnedAppId);
	}
}
