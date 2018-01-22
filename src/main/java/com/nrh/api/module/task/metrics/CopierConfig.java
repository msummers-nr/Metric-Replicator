package com.nrh.api.module.task.metrics;

import com.nrh.api.APIApplication;
import com.nrh.api.module.nr.APIKeyset;
import com.nrh.api.module.nr.dao.Application;
import com.nrh.api.module.nr.dao.Metric;
import com.typesafe.config.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopierConfig {

  private static final Logger log = LoggerFactory.getLogger(CopierConfig.class);
  
  private static final String PREFIX = "newrelic-api-client.tasks.metricsCopier";

  private APIKeyset destKeys;
  private APIKeyset sourceKeys;
  private String eventType;
  private Config conf;

  private Map<String, Application> appMap;

  public CopierConfig() {
    this.conf = APIApplication.getConfig();
    this.appMap = new HashMap<>();

    readSourceConfig();
    readDestConfig();
  }

  public Application getApplication(String appName) {
    return appMap.get(appName);
  }

  private void readSourceConfig() {
    // Property strings used
    String sProp = PREFIX + ".source";
    String sPropSourceAccount = sProp + ".account";

    // Setup the API Keys
    String sSourceAccount = APIApplication.getConfString(sPropSourceAccount);
    sourceKeys = new APIKeyset(APIApplication.getConfig(), sSourceAccount);

    // Read the metrics
    readSourceMetrics(sProp);
  }

  private void readSourceMetrics(String sProp) {
    // Get the list of metrics
    String sPropMetricList = sProp + ".metricList";
    List<String> sMetricList = conf.getStringList(sPropMetricList);

    // Create a Metric object for each metric
    List<Metric> metricList = new ArrayList<>();
    for (String shortName : sMetricList) {
      String sPropMetricFullName = sProp + ".metrics." + shortName + ".mName";
      String fullName = conf.getString(sPropMetricFullName);
      Metric metric = new Metric(fullName, shortName);
      metricList.add(metric);
      log.info("* adding metric: " + metric);
    }

    readSourceApps(sProp, metricList);
  }

  private void readSourceApps(String sProp, List<Metric> metricList) {
    // Get the list of apps
    String sPropAppList = sProp + ".applicationList";
    List<String> sAppList = conf.getStringList(sPropAppList);
    
    // Create an Application object for each app
    for (String appName : sAppList) {
      String sPropAppId = sProp + ".applications." + appName + ".appId";
      int iAppId = conf.getInt(sPropAppId);
      Application app = new Application(iAppId, appName);
      app.setMetricList(metricList);
      appMap.put(appName, app);
      log.info("* adding application: " + app);
    }
  }

  private void readDestConfig() {
    
    // Property strings used
    String sProp = PREFIX + ".dest";
    String sPropDestAccount = sProp + ".account";
    String sPropEventType = sProp + ".eventType";

    // Setup the API Keys
    String sDestAccount = APIApplication.getConfString(sPropDestAccount);
    destKeys = new APIKeyset(APIApplication.getConfig(), sDestAccount);

    // Reading string values
    eventType = APIApplication.getConfString(sPropEventType);
  }

  public APIKeyset getDestKeys() {
    return destKeys;
  }
  public APIKeyset getSourceKeys() {
    return sourceKeys;
  }
  public String getEventType() {
    return eventType;
  }
  public Map<String, Application> getAppMap() {
    return appMap;
  }
}