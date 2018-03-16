package com.nrh.api.module.task.metrics;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@ConditionalOnProperty(prefix = "newrelic-api-client.tasks.metricsCopier", name = "enabled")
public class MetricsCopier {

	private static final Logger log = LoggerFactory.getLogger(MetricsCopier.class);

	@Autowired
	private CopierConfig copierConfig;
	@Autowired
	private ExtractInsights extractInsights;
	@Autowired
	private ExtractMetrics extractMetrics;
	// private Transform transform;
	@Autowired
	private Load load;

	public MetricsCopier() throws IOException {
	}

	/**
	 * This task runs at :00 and :30 every minute
	 */
	@Scheduled(cron = "*/30 * * * * *")
	@Trace(dispatcher = true)
	public void copy() throws IOException {
		log.info("copy: enter");
		// Extract the current host/instance ids (if applicable) and get metrics
		// FIXME this is pretty heavy, does it *have* to be done on each iteration?
		ArrayList<MetricConfig> metricConfigList = extractMetrics.prepConfig();
		log.debug("copy: thread count: {}", metricConfigList.size());

		// Spin-up the metric queries onto threads
		ArrayList<CompletableFuture<ArrayList<MetricDataModel>>> completableFutures = new ArrayList<>();
		for (MetricConfig metricConfig : metricConfigList) {
			ArrayList<MetricConfig> _metricConfigList = new ArrayList<>();
			_metricConfigList.add(metricConfig);
			CompletableFuture<ArrayList<MetricDataModel>> completableFuture = extractMetrics.queryMetricData(_metricConfigList);
			completableFutures.add(completableFuture);
		}
		log.info("copy: futures created");

		// Wait for the threads to complete
		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture<?>[1])).join();
		log.info("copy: futures joined");

		ArrayList<MetricDataModel> metricDataList = new ArrayList<>();
		// Gather the results
		for (CompletableFuture<ArrayList<MetricDataModel>> completableFuture : completableFutures) {
			try {
				metricDataList.addAll(completableFuture.get());
			} catch (Exception e) {
				// *SHOULD* never see this as we joined above
				log.error(e.getMessage(), e);
			}
		}
		log.info("copy: results gathered");

		// Extract the latest values from Insights
		Map<String, Date> latestMap = extractInsights.queryInsights();
		log.info("copy: have Insights timestamps");
		Transform transform = new Transform(copierConfig.getEventType());
		ArrayList<Event> eventList = transform.toEvents(latestMap, metricDataList);
		log.info("copy: events created");
		load.post(eventList);
		// Clean up
		log.info("copy: exit");
	}
}