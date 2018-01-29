package com.nrh.api.module.nr.client.rest;

import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppAPI extends AppBase {

	private static final Logger log = LoggerFactory.getLogger(AppAPI.class);
			
	public static final String URL_LIST_PATH = "v2/applications.json";
	public static final String URL_SHOW_PATH = "v2/applications/{application_id}.json";
	public static final String URL_METRICS_PATH = "v2/applications/{application_id}/metrics.json";
	public static final String URL_DATA_PATH = "v2/applications/{application_id}/metrics/data.json";
	
	public AppAPI(APIKeyset keys) {
		super(keys);
		log.info("Applications API constructed");
	}
	
	public ArrayList<AppModel> list(AppConfig appConfig) throws IOException {
		// Must specify this is the Applications type
		appConfig.setConfigType(AppConfig.TYPE_APP_ONLY);
		
		// Static URL for this list call
		String sResponse = list(appConfig, URL_LIST_PATH);
		return ParseAppList.strToAppList(sResponse, appConfig);

	}

	public AppModel show(AppConfig appConfig) throws IOException {
		// Create the URL segment
		String sAppId = appConfig.getAppId().toString();
		String showSegment = URL_SHOW_PATH.replace("{application_id}", sAppId);
		
		// Must specify this is the Applications type
		appConfig.setConfigType(AppConfig.TYPE_APP_ONLY);
		String sResponse = show(appConfig, showSegment);
		
		// Parse the response correctly
		return ParseAppShow.strToAppModel(sResponse, appConfig);

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
