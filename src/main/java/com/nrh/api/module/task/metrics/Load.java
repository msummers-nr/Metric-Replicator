package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.ArrayList;
import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.Insights;
import com.nrh.api.module.nr.dao.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Load {
  
  private static final Logger log = LoggerFactory.getLogger(Load.class);
  private Insights insights;

  public Load(CopierConfig config) {
    insights = new Insights(config.getDestKeys());
  }

  @Trace
  public void post(ArrayList<Event> eventList) throws IOException {
    if (eventList.size() > 0) {
      // log.info("There are " + eventList.size() + " events to post");
      String sResponse = insights.insertSync(eventList);
      log.info(sResponse);
    }
  }
}