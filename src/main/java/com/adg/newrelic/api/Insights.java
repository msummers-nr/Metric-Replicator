package com.adg.newrelic.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Insights {
	
	private static final Logger log = LoggerFactory.getLogger(Insights.class);
	
	public static final String URL_SCHEME = "https";
	public static final String URL_QUERY_HOST = "insights-api.newrelic.com";
	public static final String URL_QUERY_PATH = "v1/accounts/{accountId}/query";
	
	private APIKeyset keys;
	private OkHttpClient client;
	
	public Insights(APIKeyset keys) {
		this.keys = keys;
		client = new OkHttpClient();
	}

	private Request makeQueryRequest(String nrql) {
		// Replace the accountId in the path
		String path = URL_QUERY_PATH.replace("{accountId}", keys.getAccountId());
		
		// Create the URL with the proper path and query parameter
		HttpUrl url = new HttpUrl.Builder()
			.scheme(URL_SCHEME)
			.host(URL_QUERY_HOST)
			.addPathSegments(path)
			.addQueryParameter("nrql", nrql)
			.build();
		
		// Create the request
		Request req = new Request.Builder()
			.url(url)
			.addHeader("X-Query-Key", keys.getInsightsQueryKey())
			.build();
		return req;
	}
	
	public Response querySync(String nrql) throws IOException {
		
		// Use the helper to make the Request object
		Request req = makeQueryRequest(nrql);
		
		// Synchronous call
		return Util.callSync(client, req);
	}
	
	public void queryAsync(String nrql, Callback cb) throws IOException {
		// Use the helper to make the Request object
		Request req = makeQueryRequest(nrql);
		
		// Asynchronous call sends response to callback
		client.newCall(req).enqueue(cb);
	}
}