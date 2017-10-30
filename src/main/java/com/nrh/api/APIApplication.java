package com.nrh.api;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class APIApplication {

	private static final Logger log = LoggerFactory.getLogger(APIApplication.class);
	
	private static final String PREFIX = "newrelic-api-client.";
	private static final String EXAMPLE_ENABLED = "tasks.exampleTask.enabled";
	private static final String SYNTHETICS_COPIER_ENABLED = "tasks.syntheticsCopier.enabled";

	private static Config conf;

	public static void main(String[] args) {

		// Read the config and initialize
		conf = ConfigFactory.load();
		log.info("Config file used: " + conf.origin());

		// These system properties control which tasks are enabled
		System.setProperty(EXAMPLE_ENABLED, getConfString(EXAMPLE_ENABLED));
		System.setProperty(SYNTHETICS_COPIER_ENABLED,  getConfString(SYNTHETICS_COPIER_ENABLED));
		
		// Start the Spring Boot application
		SpringApplication.run(APIApplication.class, args);
	}

	@PostConstruct
	private static void init() {
		log.info("Example Task Enabled = " + System.getProperty(EXAMPLE_ENABLED));
		log.info("Synthetics Copier Task Enabled = " + System.getProperty(SYNTHETICS_COPIER_ENABLED));
	}

	public static String getConfString(String key) {
		String path = PREFIX + key;
		return conf.getString(path);
	}

	public static Config getConfig() {
		return conf;
	}
}
