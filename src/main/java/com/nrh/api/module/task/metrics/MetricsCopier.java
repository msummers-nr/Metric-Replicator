package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.config.MetricConfig;
import com.nrh.api.module.nr.model.Event;
import com.nrh.api.module.nr.model.MetricDataModel;

import reactor.core.publisher.Mono;

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

	public MetricsCopier() {
	}

	/**
	 * This task runs at :00 and :30 every minute
	 */
	@SuppressWarnings("unchecked")
	@Scheduled(cron = "*/30 * * * * *")
	@Trace(dispatcher = true)
	public void copy() throws IOException {
		log.info("copy: enter");
		ArrayList<MetricConfig> metricConfigs = extractMetrics.prepConfig();
		log.info("copy: metricConfigs.size: {}", metricConfigs.size());

		List<Mono<List<MetricDataModel>>> monos = new ArrayList<>(metricConfigs.size());
		ArrayList<MetricConfig> tmp = new ArrayList<>(1);
		tmp.add(metricConfigs.get(0));
		ArrayList<MetricDataModel> metricDataList = new ArrayList<>();
		for (MetricConfig metricConfig : metricConfigs) {
			// for (MetricConfig metricConfig : tmp) {
			try {
				monos.add(extractMetrics.queryMetricData(metricConfig)
				      .doOnSuccess(result -> {
					      metricDataList.addAll(result);
				      })
				      .cache());
				// log.info("copy: created mono: {}", mono.toString());
			} catch (Exception e) {
				log.error("copy: mono creation loop: {}", e.getMessage(), e);
			}
		}
		log.info("copy: monos created: {}", monos.size());

		Mono.when(monos)
		      // .doFinally(onFinally -> {
		      // Map<String, Date> latestMap;
		      // try {
		      // log.info("copy: mono: when: enter: metricDataList: {}", metricDataList.size());
		      // latestMap = extractInsights.queryInsights();
		      // log.info("copy: mono: when: have Insights timestamps: {}", latestMap.size());
		      //
		      // Transform transform = new Transform(copierConfig.getEventType());
		      // ArrayList<Event> eventList = transform.toEvents(latestMap, metricDataList);
		      // log.info("copy: when: events created: {}", eventList.size());
		      //
		      // load.post(eventList);
		      // } catch (Exception e) {
		      // log.error("copy: mono: when: {}", e.getMessage(), e);
		      // }
		      // log.info("copy: exit");
		      // })
		      .block();
		// Mono.zip(results -> {
		// for (Object result : results) {
		// if (result instanceof List<?>) {
		// for (ArrayList<MetricDataModel> metricDataModel : (List<?>) result) {
		// metricDataList.addAll(metricDataModel);
		// }
		// } else {
		// log.warn("copy: zip: results: result type: {}", result.getClass());
		// }
		// }
		// return true;
		// }, monos.toArray(new Mono<?>[1]))
		// .block();
		 log.info("copy: monos complete: metricDataList.size: {}", metricDataList.size());
		 
		 Map<String, Date> latestMap = extractInsights.queryInsights();
		 log.info("copy: have Insights timestamps");
		 
		 Transform transform = new Transform(copierConfig.getEventType());
		 ArrayList<Event> eventList = transform.toEvents(latestMap, metricDataList);
		 log.info("copy: events created");
		
		 load.post(eventList);
		 log.info("copy: exit");
	}

	// public void copy2() throws IOException {
	// log.info("copy: enter");
	// // Extract the current host/instance ids (if applicable) and get metrics
	// // FIXME this is pretty heavy, does it *have* to be done on each iteration?
	// ArrayList<MetricConfig> metricConfigList = extractMetrics.prepConfig();
	// log.debug("copy: thread count: {}", metricConfigList.size());
	//
	// // Spin-up the metric queries onto threads
	// ArrayList<CompletableFuture<ArrayList<MetricDataModel>>> completableFutures = new ArrayList<>();
	// for (MetricConfig metricConfig : metricConfigList) {
	// ArrayList<MetricConfig> _metricConfigList = new ArrayList<>();
	// _metricConfigList.add(metricConfig);
	// CompletableFuture<ArrayList<MetricDataModel>> completableFuture = extractMetrics.queryMetricData(_metricConfigList);
	// completableFutures.add(completableFuture);
	// }
	// log.info("copy: futures created");
	//
	// // Wait for the threads to complete
	// CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture<?>[1]))
	// .join();
	// log.info("copy: futures joined");
	//
	// ArrayList<MetricDataModel> metricDataList = new ArrayList<>();
	// // Gather the results
	// for (CompletableFuture<ArrayList<MetricDataModel>> completableFuture : completableFutures) {
	// try {
	// metricDataList.addAll(completableFuture.get());
	// } catch (Exception e) {
	// // *SHOULD* never see this as we joined above
	// log.error(e.getMessage(), e);
	// }
	// }
	// log.info("copy: results gathered");
	//
	// // Extract the latest values from Insights
	// Map<String, Date> latestMap = extractInsights.queryInsights();
	// log.info("copy: have Insights timestamps");
	// Transform transform = new Transform(copierConfig.getEventType());
	// ArrayList<Event> eventList = transform.toEvents(latestMap, metricDataList);
	// log.info("copy: events created");
	// load.post(eventList);
	// // Clean up
	// log.info("copy: exit");
	// }
}