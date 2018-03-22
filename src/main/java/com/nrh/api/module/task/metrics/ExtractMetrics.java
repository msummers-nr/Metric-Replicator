package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.client.rest.*;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import reactor.core.publisher.Mono;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.annotation.PostConstruct;

// DIRE WARNING:  Beware of instance vars as this class has a multi-threaded method!!!!
@Service
public class ExtractMetrics {
	private static final Logger log = LoggerFactory.getLogger(ExtractMetrics.class);

	@Autowired
	private CopierConfig copierConfig;

	public ExtractMetrics() {
	}

	private ArrayList<MetricConfig> cfgExpand(MetricConfig metricConfig) throws IOException {
		ArrayList<MetricConfig> resultList = new ArrayList<>();
		String configType = metricConfig.getConfigType();

		// If it's app only there's no need to expand
		if (configType.equals(AppConfig.TYPE_APP_ONLY)) {
			resultList.add(metricConfig);
		}

		// Fetch the host ids and make a config for each one
		if (configType.equals(AppConfig.TYPE_APP_HOST)) {
			AppHostAPI apiHostClient = new AppHostAPI(copierConfig.getSourceKeys());
			ArrayList<AppHostModel> appHostList = apiHostClient.list(metricConfig);
			for (AppHostModel appHostModel : appHostList) {
				resultList.add(hostModelToConfig(appHostModel, metricConfig));
			}
		}

		// Feth the instance ids and make a config for each one
		if (configType.equals(AppConfig.TYPE_APP_INSTANCE)) {
			AppInstanceAPI apiInstanceClient = new AppInstanceAPI(copierConfig.getSourceKeys());
			ArrayList<AppInstanceModel> appInstanceList = apiInstanceClient.list(metricConfig);
			for (AppInstanceModel appInstanceModel : appInstanceList) {
				resultList.add(instanceModelToConfig(appInstanceModel, metricConfig));
			}
		}
		return resultList;
	}

	private void csvToConfig(CSVMetric csvMetric, MetricConfig metricConfig) {
		// Populate the config
		metricConfig.setAppId(csvMetric.getAppId());
		metricConfig.setAppName(csvMetric.getAppName());
		metricConfig.setConfigType(csvMetric.getConfigType());
		String fullName = csvMetric.getMetricName();
		String shortName = csvMetric.getShortName();
		metricConfig.addMetricName(fullName, shortName);
	}

	private Collection<MetricConfig> csvToConfigList() {
		HashMap<Integer, MetricConfig> metricMap = new HashMap<>();

		// Start with the list from the config
		List<CSVMetric> csvMetricList = copierConfig.getCsvMetricList();
		for (CSVMetric csvMetric : csvMetricList) {

			// Create a new config if it doesn't exist for this appId
			Integer appId = csvMetric.getAppId();
			MetricConfig metricConfig = metricMap.getOrDefault(appId, new MetricConfig());
			csvToConfig(csvMetric, metricConfig);
			metricMap.put(appId, metricConfig);
		}

		return metricMap.values();
	}

	private MetricConfig hostModelToConfig(AppHostModel appHostModel, MetricConfig oldConfig) {
		try {
			MetricConfig newConfig = (MetricConfig) oldConfig.clone();
			newConfig.setHost(appHostModel.getHost());
			newConfig.setHostId(appHostModel.getHostId());
			return newConfig;
		} catch (CloneNotSupportedException cnse) {
			log.error(cnse.getMessage());
		}
		return null;
	}

	private MetricConfig instanceModelToConfig(AppInstanceModel appInstanceModel, MetricConfig oldConfig) {
		try {
			MetricConfig newConfig = (MetricConfig) oldConfig.clone();
			newConfig.setHost(appInstanceModel.getHost());
			newConfig.setPort(appInstanceModel.getPort());
			newConfig.setInstanceId(appInstanceModel.getInstanceId());
			return newConfig;
		} catch (CloneNotSupportedException cnse) {
			log.error(cnse.getMessage());
		}
		return null;
	}

	@PostConstruct
	private void postConstruct() throws IOException {
		this.prepConfig();
	}

	ArrayList<MetricConfig> prepConfig;

	@Trace
	public ArrayList<MetricConfig> prepConfig() throws IOException {
		log.info("prepConfig: enter");
		if (prepConfig == null) {
			prepConfig = new ArrayList<>(512);
			Collection<MetricConfig> metricConfigList = csvToConfigList();
			log.info("prepConfig: metricConfigList.size: {}", metricConfigList.size());
			for (MetricConfig metricConfig : metricConfigList) {
				prepConfig.addAll(cfgExpand(metricConfig));
			}
			log.info("prepConfig: exit: resultList.size: {}", prepConfig.size());
			AppBase.setPoolSize(prepConfig.size());
		}
		return prepConfig;
	}

	@Trace
	@Async
	public CompletableFuture<ArrayList<MetricDataModel>> queryMetricData(ArrayList<MetricConfig> metricConfigs) throws IOException {
		log.info("queryMetricData: enter: metricConfigs.size: {}", metricConfigs.size());
		// FIXME figure-out how to set the size to something rational
		ArrayList<MetricDataModel> resultList = new ArrayList<>(1024);
		try {
			for (MetricConfig metricConfig : metricConfigs) {
				ArrayList<MetricDataModel> metricDataList = runProperQuery(metricConfig);
				resultList.addAll(metricDataList);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("queryMetricData: exit: resultList.size: {}", resultList.size());
		return CompletableFuture.completedFuture(resultList);
	}

	private ArrayList<MetricDataModel> runProperQuery(MetricConfig metricConfig) throws IOException {
		log.debug("runProperQuery: enter");
		String configType = metricConfig.getConfigType();
		ArrayList<MetricDataModel> result;
		if (configType.equals(AppConfig.TYPE_APP_HOST)) {
			AppHostAPI apiHostClient = new AppHostAPI(copierConfig.getSourceKeys());
			log.debug("runProperQuery: apiHostClient");
			result = apiHostClient.metricData(metricConfig);
		} else if (configType.equals(AppConfig.TYPE_APP_INSTANCE)) {
			AppInstanceAPI apiInstanceClient = new AppInstanceAPI(copierConfig.getSourceKeys());
			log.debug("runProperQuery: apiInstanceClient");
			result = apiInstanceClient.metricData(metricConfig);
		} else {
			AppAPI apiOnlyClient = new AppAPI(copierConfig.getSourceKeys());
			log.debug("runProperQuery: apiOnlyClient");
			result = apiOnlyClient.metricData(metricConfig);
		}
		log.debug("runProperQuery: exit");
		return result;
	}

	public Mono<List<MetricDataModel>> queryMetricData(MetricConfig metricConfig) throws Exception {
		Mono<List<MetricDataModel>> result;
		String configType = metricConfig.getConfigType();
		if (configType.equals(AppConfig.TYPE_APP_HOST)) {
			AppHostAPI apiHostClient = new AppHostAPI(copierConfig.getSourceKeys(), false);
			result = apiHostClient.metricDataMono(metricConfig);
		} else if (configType.equals(AppConfig.TYPE_APP_INSTANCE)) {
			AppInstanceAPI apiInstanceClient = new AppInstanceAPI(copierConfig.getSourceKeys(), false);
			log.debug("runProperQuery: apiInstanceClient");
			result = apiInstanceClient.metricDataMono(metricConfig);
		} else {
			AppAPI apiOnlyClient = new AppAPI(copierConfig.getSourceKeys(), false);
			log.debug("runProperQuery: apiOnlyClient");
			result = apiOnlyClient.metricDataMono(metricConfig);
		}
		log.debug("runProperQuery: exit");
		return result;
	}
}