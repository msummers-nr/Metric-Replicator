package com.nrh.api.module.nr.client.rest;

import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationsAPI extends ApplicationBase {

	private static final Logger log = LoggerFactory.getLogger(ApplicationsAPI.class);
			
	public static final String URL_LIST_PATH = "v2/applications.json";
	public static final String URL_SHOW_PATH = "v2/applications/{application_id}.json";
	public static final String URL_METRICS_PATH = "v2/applications/{application_id}/metrics.json";
	public static final String URL_DATA_PATH = "v2/applications/{application_id}/metrics/data.json";
	
	public ApplicationsAPI(APIKeyset keys) {
		super(keys);
		log.info("Applications API constructed");
	}
	
	public ArrayList<AppModel> list(ApplicationConfig appConfig) throws IOException {
		// Must specify this is the Applications type
		appConfig.setConfigType(ApplicationConfig.TYPE_APP_ONLY);
		
		// Static URL for this list call
		return list(appConfig, URL_LIST_PATH);
	}

	public AppModel show(ApplicationConfig appConfig) throws IOException {
		// Create the URL segment
		String sAppId = appConfig.getAppId().toString();
		String showSegment = URL_SHOW_PATH.replace("{application_id}", sAppId);
		
		// Must specify this is the Applications type
		appConfig.setConfigType(ApplicationConfig.TYPE_APP_ONLY);
		return show(appConfig, showSegment);
	}

	public ArrayList<MetricNameModel> metricNames(MetricConfig metricConfig) throws IOException {
		
		// Create the URL segment
		Integer appId = metricConfig.getAppId();
		String metricSegment = URL_METRICS_PATH.replace("{application_id}", appId.toString());
		return metricNames(metricConfig, metricSegment);
	}

	public ArrayList<MetricDataModel> metricData(MetricConfig metricConfig) throws IOException {
		// Create the URL segment
		Integer appId = metricConfig.getAppId();
		String dataSegment = URL_DATA_PATH.replace("{application_id}", appId.toString());
		return metricData(metricConfig, dataSegment);
	}
}
