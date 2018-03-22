package com.nrh.api.module.nr.client.rest;

import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppInstanceAPI extends AppBase {

	private static final Logger log = LoggerFactory.getLogger(AppInstanceAPI.class);

	public static final String URL_HOST = "api.newrelic.com";
	public static final String URL_LIST_PATH = "v2/applications/{application_id}/instances.json";
	public static final String URL_SHOW_PATH = "v2/applications/{application_id}/instances/{instance_id}.json";
	public static final String URL_METRICS_PATH = "v2/applications/{application_id}/instances/{instance_id}/metrics.json";
	public static final String URL_DATA_PATH = "v2/applications/{application_id}/instances/{instance_id}/metrics/data.json";

	public AppInstanceAPI(APIKeyset keys) {
		super(keys, true);
	}

	public AppInstanceAPI(APIKeyset keys, Boolean createClient) {
		super(keys, createClient);
	}

	public ArrayList<AppInstanceModel> list(AppConfig appConfig) throws IOException {
		// Create the URL segment
		String sAppId = appConfig.getAppId()
		      .toString();
		String listSegment = URL_LIST_PATH.replace("{application_id}", sAppId);

		// Must specify this is the APP_HOST type
		appConfig.setConfigType(AppConfig.TYPE_APP_INSTANCE);
		String sResponse = list(appConfig, listSegment);

		// Parse the response correctly
		return ParseAppList.strToAppInstanceList(sResponse, appConfig);
	}

	public AppInstanceModel show(AppConfig appConfig) throws IOException {
		// Create the URL segment
		String sAppId = appConfig.getAppId()
		      .toString();
		String sInstanceId = appConfig.getInstanceId()
		      .toString();
		String showSegment = URL_SHOW_PATH.replace("{application_id}", sAppId);
		showSegment = showSegment.replace("{instance_id}", sInstanceId);

		// Must specify this is the APP_HOST type
		appConfig.setConfigType(AppConfig.TYPE_APP_INSTANCE);
		String sResponse = show(appConfig, showSegment);

		// Parse the response correctly
		return ParseAppShow.strToAppInstanceModel(sResponse, appConfig);
	}

	public ArrayList<MetricNameModel> metricNames(MetricConfig metricConfig) throws IOException {

		// Create the URL segment
		String sAppId = metricConfig.getAppId()
		      .toString();
		String sInstanceId = metricConfig.getInstanceId()
		      .toString();
		String metricSegment = URL_METRICS_PATH.replace("{application_id}", sAppId);
		metricSegment = metricSegment.replace("{instance_id}", sInstanceId);
		return metricNames(metricConfig, metricSegment);
	}

	public ArrayList<MetricDataModel> metricData(MetricConfig metricConfig) throws IOException {
		// Create the URL segment
		Integer appId = metricConfig.getAppId();
		String sInstanceId = metricConfig.getInstanceId()
		      .toString();
		String dataSegment = URL_DATA_PATH.replace("{application_id}", appId.toString());
		dataSegment = dataSegment.replace("{instance_id}", sInstanceId);
		return metricData(metricConfig, dataSegment);
	}

	public Mono<List<MetricDataModel>> metricDataMono(MetricConfig metricConfig) {
		// TODO Auto-generated method stub
		return null;
	}
}