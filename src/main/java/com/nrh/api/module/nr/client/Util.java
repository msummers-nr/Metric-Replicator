package com.nrh.api.module.nr.client;

import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);
	
	public static final String URL_SCHEME = "https";

	public static final MediaType JSON_UTF8 = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType JSON_ONLY = MediaType.parse("application/json");

	public static Response callSync(OkHttpClient client, Request req) throws IOException {
		log.info("callSync: enter");
		// Synchronous call
		Response rsp = client.newCall(req).execute();
		if (!rsp.isSuccessful()) {
			log.error("Error Message: " + rsp.message());
			throw new IOException("Bad Status Code: " + rsp);
		}
		log.info("callSync: exit");
		return rsp;
	}

	public static Builder startBuilder(String host, String segment) {
		Builder urlBuilder = new HttpUrl.Builder()
			.scheme(URL_SCHEME)
			.host(host)
			.addPathSegments(segment);
		return urlBuilder;
	}

	public static void addFilters(Builder urlBuilder, Map<String, String> filterMap) {
		for (String filterName : filterMap.keySet()) {
			String filterValue = filterMap.get(filterName);
			String paramName = "filter[" + filterName + "]";
			urlBuilder.addQueryParameter(paramName, filterValue);
		}
	}

	public static Request createRequest(Builder urlBuilder, String apiKey) {
		// Create the request
		Request req = new Request.Builder()
			.url(urlBuilder.build())
			.addHeader("X-Api-Key", apiKey)
			.build();
		return req;
	}
}
