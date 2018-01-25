package com.nrh.api.module.nr.client.rest;

import com.nrh.api.module.nr.client.Util;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.HttpUrl.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApplicationsAPI {

	private static final Logger log = LoggerFactory.getLogger(ApplicationsAPI.class);
			
	public static final String URL_HOST = "api.newrelic.com";
	public static final String URL_LIST_PATH = "v2/applications.json";
	public static final String URL_SHOW_PATH = "v2/applications/{application_id}.json";
	public static final String URL_METRICS_PATH = "v2/applications/{application_id}/metrics.json";
	public static final String URL_DATA_PATH = "v2/applications/{application_id}/metrics/data.json";
	
	private APIKeyset keys;
	private OkHttpClient client;

	public ApplicationsAPI(APIKeyset keys) {
		this.keys = keys;
		client = new OkHttpClient();
		log.debug("Applications API constructed");
	}
	
	public ArrayList<AppModel> list(AppListConfig cfg) throws IOException {
		
		// Create the URL with the filters
		Builder urlBuilder = Util.startBuilder(URL_HOST, URL_LIST_PATH);
		Util.addFilters(urlBuilder, cfg.getFilterMap());

		// Create the request object and call the API
		Request req = Util.createRequest(urlBuilder, keys.getRestKey());
		Response rsp = Util.callSync(client, req);

		// Parse the Response into a list of App Models
		String sResponse = rsp.body().string();
		log.debug(sResponse);
		return Parser.strToAppList(sResponse);
	}

	public AppModel show(Integer appId) throws IOException {
		// Create the URL (no parameters)
		String showSegment = URL_SHOW_PATH.replace("{application_id}", appId.toString());
		Builder urlBuilder = Util.startBuilder(URL_HOST, showSegment);
		
		// Create the request object and call the API
		Request req = Util.createRequest(urlBuilder, keys.getRestKey());
		Response rsp = Util.callSync(client, req);

		// Parse the Response into an App Models
		String sResponse = rsp.body().string();
		log.debug(sResponse);
		return Parser.strToAppModel(sResponse);
	}

	public ArrayList<MetricNameModel> metricNames(MetricNamesConfig cfg) throws IOException {
		// Create the URL
		Integer appId = cfg.getAppId();
		String metricSegment = URL_METRICS_PATH.replace("{application_id}", appId.toString());
		Builder urlBuilder = Util.startBuilder(URL_HOST, metricSegment);
		
		// Add the paramter called name if it's defined
		if (cfg.getFilterName() != null) {
			urlBuilder.addEncodedQueryParameter("name", cfg.getFilterName());
		}

		// Create the request object and call the API
		Request req = Util.createRequest(urlBuilder, keys.getRestKey());
		Response rsp = Util.callSync(client, req);

		// Parse the Response into an App Models
		String sResponse = rsp.body().string();
		log.debug(sResponse);
		return Parser.strToMetricNames(cfg.getAppId(), sResponse);
	}

	public ArrayList<MetricDataModel> metricData(MetricDataConfig cfg) throws IOException {
		// Create the URL
		Integer appId = cfg.getAppId();
		String dataSegment = URL_DATA_PATH.replace("{application_id}", appId.toString());
		Builder urlBuilder = Util.startBuilder(URL_HOST, dataSegment);

		// Add the optional parameters if they are provided
		ArrayList<String> metricNameList = cfg.getMetricNameList();
		if (metricNameList != null) {
			for (String metricName : metricNameList ) {
				urlBuilder.addEncodedQueryParameter("names[]", metricName);
			}
		}

		// Create the request object and call the API
		Request req = Util.createRequest(urlBuilder, keys.getRestKey());
		Response rsp = Util.callSync(client, req);

		// Parse the Response into an App Models
		String sResponse = rsp.body().string();
		log.debug(sResponse);
		return Parser.strToMetricData(appId, sResponse);
	}
}
