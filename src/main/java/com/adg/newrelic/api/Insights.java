package com.adg.newrelic.api;

import java.io.IOException;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Insights {
	
	private static final Logger log = LoggerFactory.getLogger(Insights.class);
	
	public static final String URL_SCHEME = "https";
	public static final String URL_QUERY_HOST = "insights-api.newrelic.com";
	public static final String URL_QUERY_PATH = "v1/accounts/{account_id}/query";
	public static final String URL_INSERT_HOST = "insights-collector.newrelic.com";
	public static final String URL_INSERT_PATH = "v1/accounts/{account_id}/events";
	
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private APIKeyset keys;
	private OkHttpClient client;
	
	public Insights(APIKeyset keys) {
		this.keys = keys;
		client = new OkHttpClient();
	}
	
	/***
	 * Call the Insights query API for this NRQL
	 * 
	 * @param nrql
	 * @return
	 * @throws IOException
	 */
	public String querySync(String nrql) throws IOException {
		
		// Use the helper to make the Request object
		Request req = makeQueryRequest(nrql);
		
		// Synchronous call
		Response rsp = Util.callSync(client, req);
		return rsp.body().string();
	}

	/**
	 * Call the Insights Insert API to publish these events
	 * 
	 * @param jEvents
	 * @return
	 * @throws IOException
	 */
	public String insertSync(JSONArray jEvents) throws IOException {
		// Use the helper to make the Request object
		Request req = makeInsertRequest(jEvents);

		// Synchronous call
		Response rsp = Util.callSync(client, req);
		return rsp.body().string();
	}

	private Request makeQueryRequest(String nrql) {
		// Replace the accountId in the path
		String pathSegment = URL_QUERY_PATH.replace("{account_id}", keys.getAccountId());
		log.debug("Query Path Segment: " + pathSegment);
		
		// Create the URL with the proper path and query parameter
		HttpUrl httpUrl = new HttpUrl.Builder()
			.scheme(URL_SCHEME)
			.host(URL_QUERY_HOST)
			.addPathSegments(pathSegment)
			.addQueryParameter("nrql", nrql)
			.build();
		
		// Create the request
		Request req = new Request.Builder()
			.url(httpUrl)
			.addHeader("X-Query-Key", keys.getInsightsQueryKey())
			.build();
		return req;
	}

	private Request makeInsertRequest(JSONArray jEvents) {
		// Replace the accountId in the path
		String pathSegment = URL_INSERT_PATH.replace("{account_id}", keys.getAccountId());
		log.debug("Insert Path Segment: " + pathSegment);

		// Create the URL with the proper path and post value
		HttpUrl httpUrl = new HttpUrl.Builder()
			.scheme(URL_SCHEME)
			.host(URL_INSERT_HOST)
			.addPathSegments(pathSegment)
			.build();

		// Create the request
		Request req = new Request.Builder()
			.url(httpUrl)
			.addHeader("X-Insert-Key", keys.getInsightsInsertKey())
			.post(RequestBody.create(JSON, jEvents.toString()))
			.build();

		return req;
	}
	
	// public void queryAsync(String nrql, Callback cb) throws IOException {
	// 	// Use the helper to make the Request object
	// 	Request req = makeQueryRequest(nrql);
		
	// 	// Asynchronous call sends response to callback
	// 	client.newCall(req).enqueue(cb);
	// }
}