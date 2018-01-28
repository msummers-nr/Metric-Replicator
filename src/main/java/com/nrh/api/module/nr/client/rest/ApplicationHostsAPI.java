package com.nrh.api.module.nr.client.rest;

import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationHostsAPI extends ApplicationBase {

	private static final Logger log = LoggerFactory.getLogger(ApplicationHostsAPI.class);
			
	public static final String URL_HOST = "api.newrelic.com";
	public static final String URL_LIST_PATH = "v2/applications/{application_id}/hosts.json";
	public static final String URL_SHOW_PATH = "v2/applications/{application_id}/hosts/{host_id}.json";
	public static final String URL_METRICS_PATH = "v2/applications/{application_id}/hosts/{host_id}/metrics.json";
	public static final String URL_DATA_PATH = "v2/applications/{application_id}/hosts/{host_id}/metrics/data.json";
	

	public ApplicationHostsAPI(APIKeyset keys) {
		super(keys);
		log.info("Application Hosts API constructed");
	}
	
	public ArrayList<AppModel> list(ApplicationConfig appConfig) throws IOException {
		// Create the URL segment
		String sAppId = appConfig.getAppId().toString();
		String listSegment = URL_LIST_PATH.replace("{application_id}", sAppId);

		// Must specify this is the APP_HOST type
		appConfig.setConfigType(ApplicationConfig.TYPE_APP_HOST);
		return list(appConfig, listSegment);
	}

	public AppModel show(ApplicationConfig appConfig) throws IOException {
		// Create the URL segment
		String sAppId = appConfig.getAppId().toString();
		String sHostId = appConfig.getHostId().toString();
		String showSegment = URL_SHOW_PATH.replace("{application_id}", sAppId);
		showSegment = showSegment.replace("{host_id}", sHostId);

		// Must specify this is the APP_HOST type
		appConfig.setConfigType(ApplicationConfig.TYPE_APP_HOST);
		return show(appConfig, showSegment);
	}

	public ArrayList<MetricNameModel> metricNames(MetricConfig metricConfig) throws IOException {
		
		// Create the URL segment
		String sAppId = metricConfig.getAppId().toString();
		String sHostId = metricConfig.getHostId().toString();
		String metricSegment = URL_METRICS_PATH.replace("{application_id}", sAppId);
		metricSegment = metricSegment.replace("{host_id}", sHostId);
		return metricNames(metricConfig, metricSegment);
	}

	public ArrayList<MetricDataModel> metricData(MetricConfig metricConfig) throws IOException {
		// Create the URL segment
		Integer appId = metricConfig.getAppId();
		String sHostId = metricConfig.getHostId().toString();
		String dataSegment = URL_DATA_PATH.replace("{application_id}", appId.toString());
		dataSegment = dataSegment.replace("{host_id}", sHostId);
		return metricData(metricConfig, dataSegment);
	}
}