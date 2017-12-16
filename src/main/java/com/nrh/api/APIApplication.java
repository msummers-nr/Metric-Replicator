package com.nrh.api;


import com.nrh.api.module.nr.APIKeyset;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
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
	
	private static final String PREFIX = "newrelic-api-client.tasks";
	private static final String EXAMPLE_ENABLED = PREFIX + ".exampleTask.enabled";
	private static final String SYNTHETICS_COPIER_ENABLED = PREFIX + ".syntheticsCopier.enabled";
	private static final String METRICS_COPIER_ENABLED = PREFIX + ".metricsCopier.enabled";

	private static Config conf;

	public static void main(String[] args) {

		readConfig();
		
		// Start the Spring Boot application
		SpringApplication.run(APIApplication.class, args);
	}

	/**
	 * Read the config file
	 */
	public static void readConfig() {
		// Read the config and initialize
		conf = ConfigFactory.load();
		log.info("Config file used: " + conf.origin());

		// These system properties control which tasks are enabled
		System.setProperty(EXAMPLE_ENABLED, getConfString(EXAMPLE_ENABLED));
		System.setProperty(SYNTHETICS_COPIER_ENABLED,  getConfString(SYNTHETICS_COPIER_ENABLED));
		System.setProperty(METRICS_COPIER_ENABLED,  getConfString(METRICS_COPIER_ENABLED));
	}

	@PostConstruct
	private static void init() {
		log.info("Example Task Enabled = " + System.getProperty(EXAMPLE_ENABLED));
		log.info("Synthetics Copier Task Enabled = " + System.getProperty(SYNTHETICS_COPIER_ENABLED));
		log.info("Metrics Copier Task Enabled = " + System.getProperty(METRICS_COPIER_ENABLED));
	}

	public static APIKeyset getAPIKeyset(String account) {
		return new APIKeyset(conf, account);
	}

	public static String getConfString(String key) {
		log.debug("getConfString(" + key + ")");
		String confString = "";
		try {
			confString = conf.getString(key);
		} catch(ConfigException.Missing cfeMissing) {
			log.error("Ignoring: " + cfeMissing.toString());
		}
		return confString;
	}

	public static Config getConfig() {
		return conf;
	}
}
