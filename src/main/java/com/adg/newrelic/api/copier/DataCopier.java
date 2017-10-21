package com.adg.newrelic.api.copier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataCopier {
	private static final Logger log = LoggerFactory.getLogger(DataCopier.class);
	
	public static void main(String[] args) {
		log.info("Data Copier Started.");
		SpringApplication.run(DataCopier.class);
	}

}
