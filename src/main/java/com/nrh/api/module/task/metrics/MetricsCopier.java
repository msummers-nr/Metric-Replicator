package com.nrh.api.module.task.metrics;

import com.newrelic.api.agent.Trace;
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
    
    // Initialize the Transform
    // transform = new Transform(config);

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

    // Extract from both insights and the REST API
    Map<String, Date> latestMap = extractInsights.queryInsights();
    ArrayList<MetricDataModel> metricData = extractMetrics.queryMetricData();

    // Transform to events then load to the destination account
    Transform transform = new Transform(config);
    ArrayList<Event> eventList = transform.toEvents(latestMap, metricData);
    load.post(eventList);

    // Clean up
    log.info("Copy complete");
  }
}