package com.nrh.api.module.task.metrics;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="newrelic-api-client.tasks.metricsCopier", name="enabled")
public class MetricsCopier {

  private static final Logger log = LoggerFactory.getLogger(MetricsCopier.class);
  
  private CopierConfig config;
  private ExtractInsights extractInsights;
  private ExtractMetrics extractMetrics;
  // private Transform transform;
  private Load load;

  public MetricsCopier() throws IOException {
    log.info("Initializing Metrics Copier");

    // Setup the source and destination keys
    config = new CopierConfig();
    
    // Initialize the Extract objects
    extractInsights = new ExtractInsights(config);
    extractMetrics = new ExtractMetrics(config);
    
    // Initialize the Load
    load = new Load(config);
  }

  /**
   * This task runs at :00 and :30 every minute
   */
  @Scheduled(cron = "*/30 * * * * *")
  @Trace(dispatcher=true)
  public void copy() throws IOException {
    
    log.info("Copy starting");

    // Extract the latest values from Insights
    Map<String, Date> latestMap = extractInsights.queryInsights();
    
    // Extract the current host/instance ids (if applicable) and get metrics
    ArrayList<MetricConfig> metricConfigList = extractMetrics.prepConfig();
    ArrayList<MetricDataModel> metricDataList = extractMetrics.queryMetricData(metricConfigList);

    // Transform to events then load to the destination account
    Transform transform = new Transform(config.getEventType());
    ArrayList<Event> eventList = transform.toEvents(latestMap, metricDataList);
    load.post(eventList);

    // Clean up
    log.info("Copy complete");
  }
}