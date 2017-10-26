package com.adg.newrelic.copier.task.synthetics;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runtime {
  
  private static final Logger log = LoggerFactory.getLogger(Runtime.class);
  
  public static int getPid() {
    // TODO get pid from spring boot
    return 0;
  }
  
  public static String getHostName() {
    try {
      String host = InetAddress.getLocalHost().getHostName();
      return host;
    } catch (UnknownHostException e) {
      log.error(e.getLocalizedMessage(), e);
    }
    return "unknown";
  }
}
