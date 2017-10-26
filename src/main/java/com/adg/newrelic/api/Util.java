package com.adg.newrelic.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);
	
	public static final MediaType JSON_UTF8 = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType JSON_ONLY = MediaType.parse("application/json");

	/**
	 * Helper function to invoke the HTTP client and handle errors
	 * 
	 * @param client
	 * @param req
	 * @return
	 * @throws IOException
	 */
	public static Response callSync(OkHttpClient client, Request req) throws IOException {
		// Synchronous call
		Response rsp = client.newCall(req).execute();
		if (!rsp.isSuccessful()) {
			log.error("Error Message: " + rsp.message());
			throw new IOException("Bad Status Code: " + rsp);
		}
		return rsp;
	}
}
