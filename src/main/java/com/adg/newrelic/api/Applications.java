package com.adg.newrelic.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Applications {

	private static final Logger log = LoggerFactory.getLogger(Applications.class);
			
	public static final String URL_SCHEME = "https";
	public static final String URL_HOST = "api.newrelic.com";
	public static final String URL_LIST_PATH = "v2/applications.json";
	
	private static OkHttpClient client = new OkHttpClient();

	public static Response listSync(APIKeyset keys) throws IOException {
		return listSync(keys, null, null, null, null);
	}
	
	public static Response listSync(APIKeyset keys, String filterName, String filterHost, String filterIds, String filterLanguage) throws IOException {
		// Use the helper to make the Request object
		Request req = makeListRequest(keys, filterName, filterHost, filterIds, filterLanguage);
		
		// Synchronous call
		Response rsp = client.newCall(req).execute();
		if (!rsp.isSuccessful()) {
			log.error("Error Message: " + rsp.message());
			throw new IOException("Bad Status Code: " + rsp);
		}
		return rsp;
	}
	
	private static HttpUrl.Builder makeUrlBuilder() {
		// Start with the builder for the main URL
		HttpUrl.Builder builder = new HttpUrl.Builder()
				.scheme(URL_SCHEME)
				.host(URL_HOST)
				.addPathSegments(URL_LIST_PATH);
		return builder;
	}
	
	private static Request makeListRequest(APIKeyset keys,
			String filterName, String filterHost, String filterIds, String filterLanguage) {
		
		// Add optional parameters if they are provided
		HttpUrl.Builder urlBuilder = makeUrlBuilder();
		if (filterName != null) { urlBuilder.addQueryParameter("filter[name]", filterName); }
		if (filterHost != null) { urlBuilder.addQueryParameter("filter[host]", filterHost); }
		if (filterIds != null) { urlBuilder.addQueryParameter("filter[ids]", filterIds); }
		if (filterLanguage != null) { urlBuilder.addQueryParameter("filter[language]", filterLanguage); }
		
		// Create the request
		Request req = new Request.Builder()
			.url(urlBuilder.build())
			.addHeader("X-Api-Key", keys.getRestKey())
			.build();
		return req;
	}
}
