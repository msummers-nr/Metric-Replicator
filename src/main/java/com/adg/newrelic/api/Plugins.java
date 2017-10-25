package com.adg.newrelic.api;

import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Plugins {
	
	private static final Logger log = LoggerFactory.getLogger(Plugins.class);
	
	public static final String URL_SCHEME = "https";
	public static final String URL_HOST = "platform-api.newrelic.com";
	public static final String URL_PATH = "platform/v1/metrics";
	
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
		
		// Create the URL with the proper path and post value
		HttpUrl httpUrl = new HttpUrl.Builder()
			.scheme(URL_SCHEME)
			.host(URL_HOST)
			.addPathSegments(URL_PATH)
			.build();

		// Create the request
		Request req = new Request.Builder()
			.url(httpUrl)
			.addHeader("X-License-Key", keys.getLicenseKey())
			.post(RequestBody.create(Util.JSON, jMessage.toString()))
			.build();

		return req;
  }
}