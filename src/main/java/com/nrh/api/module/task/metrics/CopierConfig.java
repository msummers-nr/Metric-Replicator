package com.nrh.api.module.task.metrics;

import com.nrh.api.APIApplication;
import com.nrh.api.module.nr.config.*;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
  private List<CSVMetric> csvMetricList;

  public CopierConfig() {
    this.csvMetricList = new ArrayList<>();

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
      csvMetricList = csvToBean.parse();
      log.info("Read : " + csvMetricList.size() + " rows from " + metricFile);

    } catch (IOException ioe) {
      log.error(ioe.getMessage());
      log.error(ioe.getLocalizedMessage());
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
  public List<CSVMetric> getCsvMetricList() {
    return csvMetricList;
  }
}