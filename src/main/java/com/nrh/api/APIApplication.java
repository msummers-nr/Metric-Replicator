package com.nrh.api;

import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class APIApplication {

	private static final Logger log = LoggerFactory.getLogger(APIApplication.class);

	// FIXME Magic strings move to Config
	private static final String PREFIX = "newrelic-api-client.tasks";
	private static final String EXAMPLE_ENABLED = PREFIX + ".exampleTask.enabled";
	private static final String SYNTHETICS_COPIER_ENABLED = PREFIX + ".syntheticsCopier.enabled";
	private static final String METRICS_COPIER_ENABLED = PREFIX + ".metricsCopier.enabled";

	// FIXME inject
	private static Config config;

	public static void main(String[] args) {

		// FIXME replace with dependency injection
		readConfig();
		SpringApplication.run(APIApplication.class, args);
	}

	@Value("${executor.corePoolSize:2}")
	private int executorCorePoolSize;
	@Value("${executor.maxPoolSize:10}")
	private int executorMaxPoolSize;
	@Value("${executor.queueCapacity:500}")
	private int executorQueueCapacity;
	@Value("${executor.threadNamePrefix:ExtractMetrics-}")
	private String executorThreadNamePrefix;
	@Value("config.file")
	private String configFile;

	@Bean
	public Executor asyncExecutor() {
		log.debug("asyncExecutor");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		log.debug("asyncExecutor: corePoolSize: {}", executorCorePoolSize);
		log.debug("asyncExecutor: maxPoolSize: {}", executorMaxPoolSize);
		log.debug("asyncExecutor: queueCapacity: {}", executorQueueCapacity);
		log.debug("asyncExecutor: threadNamePrefix: {}", executorThreadNamePrefix);
		executor.setCorePoolSize(executorCorePoolSize);
		executor.setMaxPoolSize(executorMaxPoolSize);
		executor.setQueueCapacity(executorQueueCapacity);
		executor.setThreadNamePrefix(executorThreadNamePrefix);
		executor.initialize();
		return executor;
	}

	// FIXME This all moves into the Config class
	/**
	 * Read the config file
	 */
	public static void readConfig() {
		// Read the config and initialize
		config = ConfigFactory.load();
		log.info("Config file used: " + config.origin());

		// FIXME get from application.properties
		// These system properties control which tasks are enabled
		System.setProperty(EXAMPLE_ENABLED, getConfString(EXAMPLE_ENABLED));
		System.setProperty(SYNTHETICS_COPIER_ENABLED, getConfString(SYNTHETICS_COPIER_ENABLED));
		System.setProperty(METRICS_COPIER_ENABLED, getConfString(METRICS_COPIER_ENABLED));
	}

	@PostConstruct
	private static void init() {
		log.info("Example Task Enabled = " + System.getProperty(EXAMPLE_ENABLED));
		log.info("Synthetics Copier Task Enabled = " + System.getProperty(SYNTHETICS_COPIER_ENABLED));
		log.info("Metrics Copier Task Enabled = " + System.getProperty(METRICS_COPIER_ENABLED));
	}

	public static String getConfString(String key) {
		String value = "";
		try {
			value = config.getString(key);
		} catch (ConfigException.Missing cfeMissing) {
			log.error("Ignoring: " + cfeMissing.toString());
		}
		log.debug("getConfString: key: {} value: {}", key, value);
		return value;
	}

	public static Config getConfig() {
		return config;
	}
}
