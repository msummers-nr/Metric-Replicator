package com.adg.newrelic.copier.task.synthetics;

import com.adg.newrelic.api.APIKeyset;
import com.adg.newrelic.api.Insights;
import com.adg.newrelic.api.Plugins;
import com.adg.newrelic.copier.DataCopier;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="tasks.syntheticsCopier", name="enabled")
public class Copier {

  private static final Logger log = LoggerFactory.getLogger(Copier.class);
  private static final String PROP_SOURCE = "tasks.syntheticsCopier.sourceAccount";
  private static final String PROP_DEST = "tasks.syntheticsCopier.destAccount";

  public static final String GUID = "com.adg.synthetics.monitor.Synthetics";
  public static final String VERSION = "2.1.1";
  
  private Insights insights;
  private Plugins plugins;

  public Copier() {
    log.info("Initializing v" + VERSION);

    // Initialize the Insights and Plugins API objects
    String sourceAccount = DataCopier.getConfString(PROP_SOURCE);
    APIKeyset sourceKeys = new APIKeyset(DataCopier.getConfig(), sourceAccount);
    insights = new Insights(sourceKeys);
    String destAccount = DataCopier.getConfString(PROP_DEST);
    APIKeyset destKeys = new APIKeyset(DataCopier.getConfig(), destAccount);
    plugins = new Plugins(destKeys);
  }

  /**
   * This task runs at :00 and :30 every minute
   */
  @Scheduled(cron = "*/30 * * * * *")
  public void start() throws IOException {
    
    // Extract from Insights, Transform the data format
    Extract extract = new Extract(insights);
    Transform transform = new Transform(extract);
    
    // Load
    plugins.postMessage(transform.toPluginMessage());
    
    // Pivot the filter data into Insights data to insert
    
  }

}

