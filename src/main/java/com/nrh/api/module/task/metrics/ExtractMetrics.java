package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nrh.api.module.nr.RestClient;
import com.nrh.api.module.nr.dao.*;

public class ExtractMetrics {
  
  private static final Logger log = LoggerFactory.getLogger(ExtractMetrics.class);
  
  private RestClient srcRestClient;
  private CopierConfig copierConfig;
  // private Map<String, Application> appMap;

  public ExtractMetrics(CopierConfig copierConfig) {
    
    // Initialize the API clients
    this.copierConfig = copierConfig;
    srcRestClient = new RestClient(copierConfig.getSourceKeys());
  }
  
  public Map<String, Application> queryMetricData() throws IOException {
    
    // Loop through the applications
    Map<String, Application> appMap = copierConfig.getAppMap();
    log.info("About to query metric data from: " + appMap.size() + " apps.");
    for (String appName : appMap.keySet()) {
      Application app = appMap.get(appName);
      
      // Query the metrics for this app
      String sResponse = srcRestClient.metricDataSync(app);
      parseResponse(app, sResponse);
    }
    log.info("Finished querying metric data.");
    return appMap;
  }

  private void parseResponse(Application app, String sResponse) {
    ArrayList<Metric> metricList = app.getMetricList();

    for (Metric metric : metricList) {
      metric.parseString(sResponse);
      Timeslice ts = metric.getLastTimeslice();
      log.debug("* Metric: " + metric + " last timeslice: " + ts.getTo());
    }
  }
}
