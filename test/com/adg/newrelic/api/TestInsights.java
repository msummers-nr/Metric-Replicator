package com.adg.newrelic.api;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestInsights {

	APIKeyset keys;
	
	@Before
	public void setUp() throws Exception {
		
		// Read in the config files
		Config conf = ConfigFactory.load();
		
		// Get the first config from the array
		List<String> configArr = conf.getStringList("newrelic-api-lib.configArr");
		String configId = "newrelic-api-lib." + configArr.get(0);
		
		// Create the API Keyset from the config file
		keys = new APIKeyset();
		String accountId = conf.getString(configId + ".accountId");
		keys.setAccountId(accountId);
		String insightsQueryKey = conf.getString(configId + ".insightsQueryKey");
		keys.setInsightsQueryKey(insightsQueryKey);
		System.out.println("Insights Test using keyset for account: " + keys.getAccountId());
		
	}

	@Test
	public void testQuery() throws IOException {
		String nrql = "SELECT count(*) FROM Transaction";
		String sResponse = Insights.query(keys, nrql);
//		System.out.println(sResponse);
		
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray jResults = jResponse.getJSONArray("results");
		Integer iCount = jResults.getJSONObject(0).getInt("count");
		assertNotNull(iCount);
		System.out.println("Count is: " + iCount.toString());
	}

}
