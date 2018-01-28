package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.client.rest.AppAPI;
import com.nrh.api.module.nr.config.MetricConfig;
import com.nrh.api.module.nr.model.*;

public class ExtractMetrics {
  
  private static final Logger log = LoggerFactory.getLogger(ExtractMetrics.class);
  
  private AppAPI srcRestClient;
  private CopierConfig copierConfig;

  public ExtractMetrics(CopierConfig copierConfig) {
    
    // Initialize the API clients
    this.copierConfig = copierConfig;
    srcRestClient = new AppAPI(copierConfig.getSourceKeys());
  }
  
  @Trace
  public ArrayList<MetricDataModel> queryMetricData() throws IOException {
    
    ArrayList<MetricDataModel> resultList = new ArrayList<>();

    // Loop through the metric data configs
    ArrayList<MetricConfig> cfgList = copierConfig.getCfgList();
    log.info("About to query metric data from: " + cfgList.size() + " apps.");

    for (MetricConfig cfg : cfgList) {
      ArrayList<MetricDataModel> metricData = srcRestClient.metricData(cfg);
      resultList.addAll(metricData);
    }

    log.info("Finished querying metric data for " + resultList.size() + " metrics.");
    return resultList;
  }
}
