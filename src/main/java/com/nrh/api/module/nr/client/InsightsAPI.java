package com.nrh.api.module.nr.client;

import com.nrh.api.module.nr.config.APIKeyset;
import com.nrh.api.module.nr.model.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InsightsAPI {
	
	private static final Logger log = LoggerFactory.getLogger(InsightsAPI.class);
	
	public static final String URL_SCHEME = "https";
	public static final String URL_QUERY_HOST = "insights-api.newrelic.com";
	public static final String URL_QUERY_PATH = "v1/accounts/{account_id}/query";
	public static final String URL_INSERT_HOST = "insights-collector.newrelic.com";
	public static final String URL_INSERT_PATH = "v1/accounts/{account_id}/events";
	
	private APIKeyset keys;
	private OkHttpClient client;
	
	public InsightsAPI(APIKeyset keys) {
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
		Request req = prepQueryRequest(nrql);
		
		// Synchronous call
		Response rsp = Util.callSync(client, req);
		return rsp.body().string();
	}

	private String insertSubList(List<Event> subList) throws IOException {
		// Turn into JSON
		JSONArray jEvents = new JSONArray();
		for (Event event : subList) {
			jEvents.put(event.toJSON());
		}
		
		// Use the helper to make the Request object
		log.info("Publishing " + jEvents.length() + " events");
		Request req = prepInsertRequest(jEvents);

		// Synchronous call
		Response rsp = Util.callSync(client, req);
		return rsp.body().string();
	}

	/**
	 * Call the Insights Insert API to publish this list of Event objects
	 */
	public List<String> insert(List<Event> eventList) throws IOException {
		
		List<String> resultList = new ArrayList<>();

		// Batch in groups of 1000
		for (int fromIndex = 0; fromIndex < eventList.size(); fromIndex+=1000) {
			// Figure out the end
			int toIndex = Math.min(fromIndex + 1000, eventList.size());
			List<Event> subList = eventList.subList(fromIndex, toIndex);
			String sResult = insertSubList(subList);
			resultList.add(sResult);
		}
		return resultList;
	}

	private Request prepQueryRequest(String nrql) {
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

	private Request prepInsertRequest(JSONArray jEvents) {
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
			.post(RequestBody.create(Util.JSON_UTF8, jEvents.toString()))
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