package com.nrh.api.module.task.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.client.InsightsAPI;
import com.nrh.api.module.nr.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class Load {

	private static final Logger log = LoggerFactory.getLogger(Load.class);
	private InsightsAPI insights;
	@Autowired
	CopierConfig copierConfig;

	public Load() {
	}

	@PostConstruct
	private void postConstruct() {
		insights = new InsightsAPI(copierConfig.getDestKeys());
	}

	// void return means no Future required
	@Trace
	@Async
	public void post(ArrayList<Event> eventList) throws IOException {
		log.debug("post: enter: eventList.size: {}", eventList.size());
		if (eventList.size() > 0) {
			List<String> responseList = insights.insert(eventList);
			for (String sResponse : responseList) {
				log.trace("post: response: {}", sResponse);
			}
		}
		log.debug("post: exit");
	}
}