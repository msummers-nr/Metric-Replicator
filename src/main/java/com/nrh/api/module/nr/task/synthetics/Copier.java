package com.nrh.api.module.nr.task.synthetics;

import com.nrh.api.APIApplication;
import com.nrh.api.module.nr.APIKeyset;
import com.nrh.api.module.nr.Insights;
import com.nrh.api.module.nr.Plugins;

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
    String sourceAccount = APIApplication.getConfString(PROP_SOURCE);
    APIKeyset sourceKeys = new APIKeyset(APIApplication.getConfig(), sourceAccount);
    insights = new Insights(sourceKeys);
    String destAccount = APIApplication.getConfString(PROP_DEST);
    APIKeyset destKeys = new APIKeyset(APIApplication.getConfig(), destAccount);
    plugins = new Plugins(destKeys);
  }

  /**
   * This task runs at :00 and :30 every minute
   */
  @Scheduled(cron = "*/30 * * * * *")
  public void start() throws IOException {
    
    // Extract from Insights
    Extract extract = new Extract(insights);
    
    // Transform the data format (for Plugin and Insights)
    Transform transform = new Transform(extract);
    
    // Load the data into Plugin API and Insights Insert API
    plugins.postMessage(transform.toPluginFormat());
    insights.insertSync(transform.toInsightsFormat());
  }

}

