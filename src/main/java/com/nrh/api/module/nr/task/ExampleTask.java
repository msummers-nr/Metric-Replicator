package com.nrh.api.module.nr.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="exampleTask.example", name="enabled")
public class ExampleTask {

  private static final Logger log = LoggerFactory.getLogger(ExampleTask.class);

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  /**
   * This task runs at :00 and :30 every minute
   */
  @Scheduled(cron = "*/30 * * * * *")
  public void reportCurrentTime() {
    log.info("The time is now {}", dateFormat.format(new Date()));
  }
}

