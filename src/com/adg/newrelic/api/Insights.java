package com.adg.newrelic.api;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Insights {
	
	public static final String URL_SCHEME = "https";
	public static final String URL_QUERY_HOST = "insights-api.newrelic.com";
	public static final String URL_QUERY_PATH = "v1/accounts/{accountId}/query";
	
	private static OkHttpClient client = new OkHttpClient();

	public static String query(APIKeyset keys, String nrql) throws IOException {
		
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
		
		Response rsp = client.newCall(req).execute();
		if (!rsp.isSuccessful()) {
			System.err.println("Error Status Code: " + rsp.code());
			System.err.println("Error Message: " + rsp.message());
		}
		return rsp.body().string();
	}
}
