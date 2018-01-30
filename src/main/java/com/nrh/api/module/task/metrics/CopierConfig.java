package com.nrh.api.module.task.metrics;

import com.nrh.api.APIApplication;
import com.nrh.api.module.nr.config.*;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
// import com.typesafe.config.Config;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
  // private Config conf;

  private ArrayList<MetricConfig> cfgList;

  public CopierConfig() {
    // this.conf = APIApplication.getConfig();
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

    // Read the CSV file
    String sPropSourceCSV = sProp + ".metricFile";
    String metricFile = APIApplication.getConfString(sPropSourceCSV);
    readMetricFile(metricFile);

    // Read the applications section, then the metrics section
    // readSourceApps(sProp);
    // readSourceMetrics(sProp);
  }

  private void readMetricFile(String metricFile) {
    try {
      // Setup the file reader and converter
      Reader reader = Files.newBufferedReader(Paths.get(metricFile));
      CsvToBean<CSVMetric> csvToBean = new CsvToBeanBuilder<CSVMetric>(reader)
        .withType(CSVMetric.class)
        .withIgnoreLeadingWhiteSpace(true)
        .build();

      // Parse the CSV file into a list of values
      List<CSVMetric> csvMetricList = csvToBean.parse();
      log.info("Read : " + csvMetricList.size() + " rows from " + metricFile);
      Map<Integer, MetricConfig> metricMap = new HashMap<>();
      for (CSVMetric csvMetric : csvMetricList) {
        String appName = csvMetric.getAppName();
        Integer appId = csvMetric.getAppId();

        // Create a new config if it doesn't exist
        MetricConfig metricConfig = metricMap.getOrDefault(appId, new MetricConfig(appId, appName));
        metricConfig.addMetricName(csvMetric.getMetricName());
        metricMap.put(appId, metricConfig);
      }

      // Add all the configs from the CSV to the main list
      cfgList.addAll(metricMap.values());

    } catch (IOException ioe) {
      log.error(ioe.getMessage());
      log.error(ioe.getLocalizedMessage());
    }
  }

  // private void readSourceMetrics(String sProp) {
  //   // Get the list of metrics
  //   String sPropMetricList = sProp + ".metricList";
  //   List<String> sMetricList = conf.getStringList(sPropMetricList);

  //   // Create a Metric object for each metric
  //   ArrayList<String> metricList = new ArrayList<>();
  //   for (String shortName : sMetricList) {
  //     String sPropMetricFullName = sProp + ".metrics." + shortName + ".mName";
  //     String fullName = conf.getString(sPropMetricFullName);
  //     metricList.add(fullName);
  //   }
  //   log.info("Loaded " + metricList.size() + " metric names from config");

  //   // Add all of the metrics to all of the configs
  //   for (MetricConfig cfg : cfgList) {
  //     cfg.setMetricNameList(metricList);
  //   }
  // }

  // private void readSourceApps(String sProp) {
  //   // Get the list of apps
  //   String sPropAppList = sProp + ".applicationList";
  //   List<String> sAppList = conf.getStringList(sPropAppList);
    
  //   // Create an Application object for each app
  //   for (String appName : sAppList) {
  //     String sPropAppId = sProp + ".applications." + appName + ".appId";
  //     Integer appId = conf.getInt(sPropAppId);
  //     MetricConfig metricConfig = new MetricConfig(appId, appName);
  //     cfgList.add(metricConfig);
  //   }

  //   log.info("Loaded " + cfgList.size() + " apps from config");
  // }

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