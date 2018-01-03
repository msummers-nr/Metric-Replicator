package com.nrh.api.module.nr;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;
import com.nrh.api.APIApplication;
import com.nrh.api.module.nr.APIKeyset;
import com.nrh.api.module.nr.RestClient;
import com.nrh.api.module.nr.dao.Metric;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRestClient {

	private static final Logger log = LoggerFactory.getLogger(TestRestClient.class);
	
	private APIKeyset keys;
	private RestClient client; 
	
	// These values are used by the ordered test cases so they are static
	private static int appId;
	private static int metricCount;
	private static ArrayList<Metric> metricList = new ArrayList<Metric>();
	private static String sResponse;

	@Before
	public void setUp() throws Exception {
		
		// Read in the config files
		APIApplication.readConfig();
		log.info("Config file used: " + APIApplication.getConfig().origin());

		// Get the name of the unitTestAccount
		String unitTestAccount = APIApplication.getConfString("newrelic-api-client.tests.unitTestAccount");
		keys = APIApplication.getAPIKeyset(unitTestAccount);
		log.info("Application API Test using keyset for account: " + keys.getAccountName());
		
		// Initialize the Applications API
		client = new RestClient(keys);
	}

	@Test
	public void test1ListSync() throws IOException {
		String sResponse = client.listSync();
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray jApplications = jResponse.getJSONArray("applications");
		log.info("Number of applications: " + jApplications.length());
		
		// There should be more than 0 applications
		assertNotEquals(0, jApplications.length());
		
		// Store the application id from the first in the array
		for (int i=0; i < jApplications.length(); i++) {
			JSONObject jApp = jApplications.getJSONObject(i);
			boolean bReporting = jApp.getBoolean("reporting");
			if (bReporting) {
				appId = jApp.getInt("id");
				break;
			}
		}
		// appId = jApplications.getJSONObject(0).getInt("id");
		log.info("Setting appId to this reporting app: " + appId);
	}
	
	@Test
	public void test2ShowSync() throws IOException {
		String sResponse = client.showSync(appId);
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(sResponse);
		int returnedAppId = jResponse.getJSONObject("application").getInt("id");
		assertEquals(appId, returnedAppId);
	}

	@Test
	public void test3MetricNamesSync() throws IOException {
		String sResponse = client.metricNamesSync(appId, null);
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray jMetrics = jResponse.getJSONArray("metrics");
		log.info("Number of metrics: " + jMetrics.length());

		// There should be more than 0 metrics
		assertNotEquals(0, jMetrics.length());

		// Grab 2 metrics
		int i = 0;
		for ( ; i < jMetrics.length(); i++) {
			// Don't get more than 5 metrics
			if (i == 5) {
				break;
			}
			
			// Add metrics to the list unless they start with Instance
			String fullName = jMetrics.getJSONObject(i).getString("name");
			if (!fullName.startsWith("Instance")) {
				Metric metric = new Metric(fullName);
				metricList.add(metric);
				metricCount++;
			}
		}
	}

	@Test
	public void test4MetricDataSync() throws IOException {

		sResponse = client.metricDataSync(appId, metricList);
		
		// Convert the response into JSON and count the number of applications
		JSONObject jResponse = new JSONObject(sResponse);
		
		// There should be {metricCount} metrics found
		JSONArray metricsFound = jResponse.getJSONObject("metric_data").getJSONArray("metrics_found");
		log.info("Count of metrics found: " + metricsFound.length());
		log.info(metricsFound.toString());

		// There should be 0 metrics not found
		JSONArray metricsNotFound = jResponse.getJSONObject("metric_data").getJSONArray("metrics_not_found");
		log.info("Count of metrics not found: " + metricsNotFound.length());
		log.info(metricsNotFound.toString());
		
		// Check that the correct number of metrics were found
		assertEquals(metricCount, metricsFound.length());
		assertEquals(0, metricsNotFound.length());
	}

	@Test
	public void test5ProcessMetricData() throws IOException {
		JSONObject jResponse = new JSONObject(sResponse);
		JSONObject jMetricData = jResponse.getJSONObject("metric_data");
		
		// Pull out each metric
		for (Metric metric : metricList) {
			metric.parseJSON(jMetricData);

			// There should be 30 timeslices in each metric
			assertEquals(30, metric.getTimesliceSize());
		}
	}
}
