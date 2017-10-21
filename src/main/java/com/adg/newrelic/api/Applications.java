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
	public static final String URL_SHOW_PATH = "v2/applications/{application_id}.json";
	public static final String URL_METRICS_PATH = "v2/applications/{application_id}/metrics.json";
	public static final String URL_DATA_PATH = "v2/applications/{application_id}/metrics/data.json";
	
	private APIKeyset keys;
	private OkHttpClient client;

	public Applications(APIKeyset keys) {
		this.keys = keys;
		client = new OkHttpClient();
	}
	
	/**
	 * Call the Applications list() synchronously with no parameters.
	 * 
	 * @return
	 * @throws IOException
	 */
	public Response listSync() throws IOException {
		return listSync(null, null, null, null);
	}
	
	/**
	 * Call the Applications list() synchronously with the filter parameters.
	 * 
	 * @param filterName
	 * @param filterHost
	 * @param filterIds
	 * @param filterLanguage
	 * @return
	 * @throws IOException
	 */
	public Response listSync(String filterName, String filterHost, String filterIds, String filterLanguage) throws IOException {
		// Use the helper to make the Request object
		Request req = makeListRequest(filterName, filterHost, filterIds, filterLanguage);
		
		// Synchronous call
		Response rsp = Util.callSync(client, req);
		return rsp;
	}
	
	/**
	 * Call the Applications show() synchronously for a given application id.

	 * @param appId
	 * @return
	 * @throws IOException
	 */
	public Response showSync(int appId) throws IOException {
		// User the helper to make the Request object
		Request req = makeShowRequest(appId);
		
		// Synchronous call
		Response rsp = Util.callSync(client, req);
		return rsp;
	}
	
	public Response metricNamesSync(int appId, String metricName) throws IOException {
		// Use the helper to make the Request object
		Request req = makeMetricNamesRequest(appId, metricName);

		// Synchronous call
		Response rsp = Util.callSync(client, req);
		return rsp;
	}
		
	private Request makeListRequest(String filterName, String filterHost, String filterIds, String filterLanguage) {
		
		// Start the initial builder
		HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
			.scheme(URL_SCHEME)
			.host(URL_HOST)
			.addPathSegments(URL_LIST_PATH);
		
		// Add optional parameters if they are provided
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
	
	private Request makeShowRequest(int appId) {
		
		// Build the URL
		String urlShow = URL_SHOW_PATH.replace("{application_id}", Integer.toString(appId));
		HttpUrl httpUrl = new HttpUrl.Builder()
			.scheme(URL_SCHEME)
			.host(URL_HOST)
			.addPathSegments(urlShow)
			.build();
		
		// Build the request
		Request req = new Request.Builder()
			.url(httpUrl)
			.addHeader("X-Api-Key", keys.getRestKey())
			.build();
		return req;
	}

	private Request makeMetricNamesRequest(int appId, String metricName) {
		// Build the URL
		String urlMetricName = URL_METRICS_PATH.replace("{application_id}", Integer.toString(appId));
		HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
			.scheme(URL_SCHEME)
			.host(URL_HOST)
			.addPathSegments(urlMetricName);
		
		// Add the optional parameters if they are provided
		if (metricName != null) { urlBuilder.addQueryParameter("name", metricName); }
		
		// Create the request
		Request req = new Request.Builder()
			.url(urlBuilder.build())
			.addHeader("X-Api-Key", keys.getRestKey())
			.build();
		return req;
	}
}