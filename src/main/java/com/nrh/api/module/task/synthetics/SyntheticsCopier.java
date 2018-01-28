package com.nrh.api.module.task.synthetics;

import com.newrelic.api.agent.Trace;
import com.nrh.api.APIApplication;
import com.nrh.api.module.nr.config.APIKeyset;
import com.nrh.api.module.nr.client.InsightsAPI;
import com.nrh.api.module.nr.client.PluginsAPI;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="newrelic-api-client.tasks.syntheticsCopier", name="enabled")
public class SyntheticsCopier {

  private static final Logger log = LoggerFactory.getLogger(SyntheticsCopier.class);
  private static final String PREFIX = "newrelic-api-client.tasks.syntheticsCopier";
  private static final String PROP_SOURCE = PREFIX + ".source.account";
  private static final String PROP_DEST = PREFIX + ".dest.account";

  public static final String GUID = "com.adg.synthetics.monitor.Synthetics";
  public static final String VERSION = "2.1.1";
  
  private InsightsAPI insights;
  private PluginsAPI plugins;

  public SyntheticsCopier() {
    log.info("Initializing v" + VERSION);

    // Initialize the Insights and Plugins API objects
    String sourceAccount = APIApplication.getConfString(PROP_SOURCE);
    APIKeyset sourceKeys = new APIKeyset(APIApplication.getConfig(), sourceAccount);
    insights = new InsightsAPI(sourceKeys);
    String destAccount = APIApplication.getConfString(PROP_DEST);
    APIKeyset destKeys = new APIKeyset(APIApplication.getConfig(), destAccount);
    plugins = new PluginsAPI(destKeys);
  }

  /**
   * This task runs at :00 and :30 every minute
   */
  @Scheduled(cron = "*/30 * * * * *")
  @Trace(dispatcher=true)
  public void start() throws IOException {
    
    log.info("Synthetics plugin starting");
    
    // Extract from Insights
    Extract extract = new Extract(insights);
    
    // Transform the data format (for Plugin and Insights)
    Transform transform = new Transform(extract);
    
    // Load the data into Plugin API and Insights Insert API
    plugins.postMessage(transform.toPluginFormat());
    insights.insertSync(transform.toInsightsFormat());

    log.info("Synthetics plugin complete");
  }

}

