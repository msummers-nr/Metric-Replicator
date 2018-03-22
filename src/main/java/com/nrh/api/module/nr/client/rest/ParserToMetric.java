package com.nrh.api.module.nr.client.rest;

import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrh.api.module.nr.config.MetricConfig;
import com.nrh.api.module.nr.model.MetricDataModel;
import com.nrh.api.module.nr.model.MetricNameModel;
import com.nrh.api.module.nr.model.TimesliceModel;

public class ParserToMetric {

	// Example date: 2018-01-02T19:30:00+00:00
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
	private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	private static final Logger log = LoggerFactory.getLogger(ParserToMetric.class);

	private static ObjectMapper objectMapper = new ObjectMapper();

	public static ArrayList<MetricDataModel> fromString(String stringResponse, MetricConfig metricConfig) {
		ArrayList<MetricDataModel> result = new ArrayList<>();
		try {
			String metricType = metricConfig.getMetricType();
			JsonNode jsonTree = objectMapper.readTree(stringResponse);
			JsonNode rootNode = jsonTree.get(metricType);

			JsonNode metricsNotFound = rootNode.get("metrics_not_found");
			if (metricsNotFound.isArray())
				for (JsonNode node : metricsNotFound)
					log.error("fromReader: metric not found: {}", node.asText());

			JsonNode metrics = rootNode.get("metrics");
			result = getMetrics(metricConfig, metrics);
		} catch (Exception e) {
			log.error("fromReader: {}", e.getMessage());
		}
		return result;
	}

	private static ArrayList<MetricDataModel> getMetrics(MetricConfig metricConfig, JsonNode metrics) {
		ArrayList<MetricDataModel> metricList = new ArrayList<>();
		if (metrics.isArray())
			for (JsonNode node : metrics) {
				String fullName = node.get("name")
				      .asText();
				String shortName = metricConfig.getShortName(fullName);
				MetricDataModel metric = new MetricDataModel(metricConfig, fullName, shortName);
				metricList.add(metric);
				getTimeSlices(metric, node.get("timeslices"));
			}
		return metricList;
	}

	private static Date getTimesliceDate(JSONObject jTimeslice) {
		// FIXME
		Date dReturn = null;
		try {
			String sTo = jTimeslice.getString("to");
			dReturn = df.parse(sTo);
		} catch (Exception pe) {
			log.debug("getTimeSliceDate: {}", pe.getMessage());
		}
		return dReturn;
	}

	private static Date getTimeSliceDate(JsonNode timeslice) {
		// FIXME
		Date dReturn = null;
		try {
			String sTo = timeslice.get("to")
			      .asText();
			dReturn = df.parse(sTo);
		} catch (Exception pe) {
			log.debug("getTimeSliceDate: {}", pe.getMessage());
		}
		return dReturn;
	}

	private static TimesliceModel getTimeSliceModel(JsonNode timeslice) {
		TimesliceModel timeSliceModel = new TimesliceModel();
		JsonNode values = timeslice.get("values");
		Iterator<Map.Entry<String, JsonNode>> fields = values.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> entry = fields.next();
			timeSliceModel.addValue(entry.getKey(), entry.getValue()
			      .asDouble());
		}

		// Iterator<String> iter = jValues.keys();
		// while (iter.hasNext()) {
		// String key = iter.next();
		// Double value = jValues.getDouble(key);
		// timeSliceModel.addValue(key, value);
		// }
		return timeSliceModel;
	}

	private static void getTimeSlices(MetricDataModel metric, JsonNode timeslices) {
		if (timeslices.isArray())
			for (JsonNode timeslice : timeslices) {
				Date date = getTimeSliceDate(timeslice);
				TimesliceModel timeSliceModel = getTimeSliceModel(timeslice);
				metric.addTimeslice(date, timeSliceModel);
			}
	}

	private static ArrayList<MetricDataModel> parseMetricArr(MetricConfig metricConfig, JSONArray jMetricArr) {
		ArrayList<MetricDataModel> metricList = new ArrayList<>();
		for (int i = 0; i < jMetricArr.length(); i++) {
			JSONObject jMetric = jMetricArr.getJSONObject(i);
			String fullName = jMetric.getString("name");
			String shortName = metricConfig.getShortName(fullName);

			MetricDataModel metric = new MetricDataModel(metricConfig, fullName, shortName);
			metricList.add(metric);

			JSONArray jTimeslices = jMetric.getJSONArray("timeslices");
			parseTimesliceArr(metric, jTimeslices);
		}
		return metricList;
	}

	private static void parseTimesliceArr(MetricDataModel metric, JSONArray jTimeslices) {
		for (int i = 0; i < jTimeslices.length(); i++) {
			JSONObject jTimeslice = jTimeslices.getJSONObject(i);

			Date date = getTimesliceDate(jTimeslice);
			TimesliceModel ts = parseTimesliceJSON(jTimeslice);
			metric.addTimeslice(date, ts);
		}
	}

	private static TimesliceModel parseTimesliceJSON(JSONObject jTimeslice) {
		TimesliceModel ts = new TimesliceModel();
		JSONObject jValues = jTimeslice.getJSONObject("values");
		Iterator<String> iter = jValues.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Double value = jValues.getDouble(key);
			ts.addValue(key, value);
		}
		return ts;
	}

	public static ArrayList<MetricDataModel> strToMetricData(String sResponse, MetricConfig metricConfig) {
		log.trace("strToMetricData: response: {}", sResponse);
		String sJsonRoot = metricConfig.getMetricType();
		JSONObject jResponse = new JSONObject(sResponse);
		JSONObject jMetricData = jResponse.getJSONObject(sJsonRoot);
		JSONArray jMetricsNotFound = jMetricData.getJSONArray("metrics_not_found");
		for (int i = 0; i < jMetricsNotFound.length(); i++) {
			String sMetric = jMetricsNotFound.getString(i);
			log.error("Metric not found: {}", sMetric);
		}
		JSONArray jMetricArr = jMetricData.getJSONArray("metrics");
		return parseMetricArr(metricConfig, jMetricArr);
	}

	public static ArrayList<MetricNameModel> strToMetricNames(String sResponse, MetricConfig metricConfig) {
		// The metricType is "metrics" or "metric_data"
		String sJsonRoot = metricConfig.getMetricType();
		// Get the JSON format
		ArrayList<MetricNameModel> metricNameList = new ArrayList<>();
		JSONObject jResponse = new JSONObject(sResponse);
		JSONArray jMetricList = jResponse.getJSONArray(sJsonRoot);
		for (int i = 0; i < jMetricList.length(); i++) {
			JSONObject jMetric = jMetricList.getJSONObject(i);
			String fullName = jMetric.getString("name");
			String shortName = metricConfig.getShortName(fullName);
			// Create the model (result)
			MetricNameModel model = new MetricNameModel(metricConfig, fullName, shortName);
			metricNameList.add(model);
		}
		return metricNameList;
	}
}