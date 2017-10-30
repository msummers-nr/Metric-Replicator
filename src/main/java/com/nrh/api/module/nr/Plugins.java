package com.nrh.api.module.nr;

import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Plugins {
	
	private static final Logger log = LoggerFactory.getLogger(Plugins.class);
	
	public static final String URL = "https://platform-api.newrelic.com/platform/v1/metrics";
	
	private APIKeyset keys;
	private OkHttpClient client;
	
	public Plugins(APIKeyset keys) {
		this.keys = keys;
		client = new OkHttpClient();
  }

  /**
   * POST a properly formatted plugin message to New Relic
   * 
   * @param jMessage Message to report to the plugin
   * @return
   * @throws IOException
   */
  public String postMessage(JSONObject jMessage) throws IOException {
    Request req = prepPostRequest(jMessage);
    log.debug(jMessage.toString());

    // Synchronous call
		Response rsp = Util.callSync(client, req);
		return rsp.body().string();
  }

  /**
   * Helper method to build a metric name with the proper format
   * 
   * @param sComponent
   * @param sMetric
   * @param sUnits
   * @return
   */
  public static String buildMetricName(String sComponent, String sMetric, String sUnits) {
    String sFullName = "Component/" + sComponent + "/" + sMetric + "[" + sUnits + "]";
    return sFullName;
  }

  private Request prepPostRequest(JSONObject jMessage) {
		
		// The Plugin API throws HTTP 415 if you don't send along as byte[]
    RequestBody body = RequestBody.create(Util.JSON_ONLY, jMessage.toString().getBytes());
		Request req = new Request.Builder()
			.url(URL)
			.post(body)
			.addHeader("content-type", "application/json")
			.addHeader("X-License-Key", keys.getLicenseKey())
			.build();
		
		return req;
  }
}