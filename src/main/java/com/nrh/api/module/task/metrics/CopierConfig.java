package com.nrh.api.module.task.metrics;

import com.nrh.api.APIApplication;
import com.nrh.api.module.nr.config.*;
import com.typesafe.config.Config;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopierConfig {

  private static final Logger log = LoggerFactory.getLogger(CopierConfig.class);
  
  private static final String PREFIX = "newrelic-api-client.tasks.metricsCopier";

  private APIKeyset destKeys;
  private APIKeyset sourceKeys;
  private String eventType;
  private Config conf;

  private ArrayList<MetricConfig> cfgList;

  public CopierConfig() {
    this.conf = APIApplication.getConfig();
    this.cfgList = new ArrayList<>();

    readSourceConfig();
    readDestConfig();
  }

  private void readSourceConfig() {
    // Property strings used
    String sProp = PREFIX + ".source";
    String sPropSourceAccount = sProp + ".account";

    // Setup the API Keys
    String sSourceAccount = APIApplication.getConfString(sPropSourceAccount);
    sourceKeys = new APIKeyset(APIApplication.getConfig(), sSourceAccount);

    // Read the applications section, then the metrics section
    readSourceApps(sProp);
    readSourceMetrics(sProp);
  }

  private void readSourceMetrics(String sProp) {
    // Get the list of metrics
    String sPropMetricList = sProp + ".metricList";
    List<String> sMetricList = conf.getStringList(sPropMetricList);

    // Create a Metric object for each metric
    ArrayList<String> metricList = new ArrayList<>();
    for (String shortName : sMetricList) {
      String sPropMetricFullName = sProp + ".metrics." + shortName + ".mName";
      String fullName = conf.getString(sPropMetricFullName);
      metricList.add(fullName);
    }
    log.info("Loaded " + metricList.size() + " metric names from config");

    // Add all of the metrics to all of the configs
    for (MetricConfig cfg : cfgList) {
      cfg.setMetricNameList(metricList);
    }
  }

  private void readSourceApps(String sProp) {
    // Get the list of apps
    String sPropAppList = sProp + ".applicationList";
    List<String> sAppList = conf.getStringList(sPropAppList);
    
    // Create an Application object for each app
    for (String appName : sAppList) {
      String sPropAppId = sProp + ".applications." + appName + ".appId";
      Integer appId = conf.getInt(sPropAppId);
      MetricConfig cfg = new MetricConfig(appId, appName);
      cfgList.add(cfg);
    }

    log.info("Loaded " + cfgList.size() + " apps from config");
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
  public ArrayList<MetricConfig> getCfgList() {
    return cfgList;
  }
}