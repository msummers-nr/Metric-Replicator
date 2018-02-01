package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.client.InsightsAPI;
import com.nrh.api.module.nr.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Load {
  
  private static final Logger log = LoggerFactory.getLogger(Load.class);
  private InsightsAPI insights;

  public Load(CopierConfig config) {
    insights = new InsightsAPI(config.getDestKeys());
  }

  @Trace
  public void post(ArrayList<Event> eventList) throws IOException {
    if (eventList.size() > 0) {
      log.info("There are " + eventList.size() + " events to post");
      List<String> responseList = insights.insert(eventList);
      for(String sResponse : responseList) {
        log.info(sResponse);
      }
    }
  }
}