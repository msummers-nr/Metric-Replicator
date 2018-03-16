package com.nrh.api.module.nr.client.rest;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.client.Util;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.HttpUrl.Builder;
import okhttp3.*;

public abstract class AppBase {

	private static final Logger log = LoggerFactory.getLogger(AppBase.class);

	public static final String URL_HOST = "api.newrelic.com";

	private APIKeyset keys;
	private OkHttpClient client;

	public AppBase(APIKeyset keys) {
		this.keys = keys;
		client = new OkHttpClient.Builder().connectTimeout(0, TimeUnit.SECONDS).writeTimeout(0, TimeUnit.SECONDS).readTimeout(0, TimeUnit.SECONDS).build();
	}

	public String list(AppConfig appConfig, String segment) throws IOException {

		// Create the URL with the filters
		Builder urlBuilder = Util.startBuilder(URL_HOST, segment);
		Util.addFilters(urlBuilder, appConfig.getFilterMap());

		// Call the API for this URL
		String sResponse = callAPI(urlBuilder, keys.getRestKey());
		return sResponse;
	}

	public String show(AppConfig appConfig, String segment) throws IOException {
		// Create the URL (no parameters)
		Builder urlBuilder = Util.startBuilder(URL_HOST, segment);

		// Call the API for this URL
		String sResponse = callAPI(urlBuilder, keys.getRestKey());
		return sResponse;
	}

	public ArrayList<MetricNameModel> metricNames(MetricConfig metricConfig, String segment) throws IOException {

		// Create the URL
		Builder urlBuilder = Util.startBuilder(URL_HOST, segment);

		// Add the paramter called name if it's defined
		if (metricConfig.getFilterName() != null) {
			urlBuilder.addEncodedQueryParameter("name", metricConfig.getFilterName());
		}

		// Call the API for this URL
		String sResponse = callAPI(urlBuilder, keys.getRestKey());

		// Parse the response correctly
		metricConfig.setMetricType(MetricConfig.TYPE_METRIC_NAME);
		return ParserToMetric.strToMetricNames(sResponse, metricConfig);
	}

	public ArrayList<MetricDataModel> metricData(MetricConfig metricConfig, String segment) throws IOException {
		log.debug("metricData: enter");
		Builder urlBuilder = Util.startBuilder(URL_HOST, segment);

		log.debug("metricData: getMetricNameList");
		Collection<String> metricNameList = metricConfig.getMetricNameList();
		if (metricNameList != null) {
			for (String metricName : metricNameList) {
				urlBuilder.addEncodedQueryParameter("names[]", metricName);
			}
		}
		log.debug("metricData: url built");

		String sResponse = callAPI(urlBuilder, keys.getRestKey());
		log.debug("metricData: callAPI");

		metricConfig.setMetricType(MetricConfig.TYPE_METRIC_DATA);
		ArrayList<MetricDataModel> result = ParserToMetric.strToMetricData(sResponse, metricConfig);
		log.debug("metricData: exit");
		return result;
	}

	@Trace
	private String callAPI(Builder urlBuilder, String apiKey) throws IOException {
		log.info("callAPI: enter");
		Request request = Util.createRequest(urlBuilder, apiKey);
		log.info("callAPI: createRequest");
		Response response = Util.callSync(client, request);
		log.info("callAPI: callSync");
		ResponseBody body = response.body();
		log.info("callAPI: response.body");
		String sResponse = body.string();
		log.info("callAPI: body.string: {}", sResponse.length());
		log.trace("callAPI: response: {}", sResponse);
		log.info("callAPI: exit");
		return sResponse;
	}
}
