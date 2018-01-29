package com.nrh.api.module.nr.client.rest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseAppShow {
  
  private static final Logger log = LoggerFactory.getLogger(ParseAppShow.class);
  
  // Example date: 2018-01-02T19:30:00+00:00
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
  private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
  
  public static AppModel strToAppModel (String sResponse, AppConfig appConfig) throws IOException {
    
    // The configType is "application" or "application_host" or "application_instance"
    String sJsonRoot = appConfig.getConfigType();

    // Grap the proper section of the JSON
    JSONObject jResponse = new JSONObject(sResponse);
    JSONObject jApp = jResponse.getJSONObject(sJsonRoot);

    return parseAppOnly(jApp);
  }

  public static AppHostModel strToAppHostModel (String sResponse, AppConfig appConfig) throws IOException {
    
    // The configType is "application" or "application_host" or "application_instance"
    String sJsonRoot = appConfig.getConfigType();

    // Grap the proper section of the JSON
    JSONObject jResponse = new JSONObject(sResponse);
    JSONObject jApp = jResponse.getJSONObject(sJsonRoot);

    return parseAppHost(jApp);
  }

  public static AppInstanceModel strToAppInstanceModel (String sResponse, AppConfig appConfig) throws IOException {
    
    // The configType is "application" or "application_host" or "application_instance"
    String sJsonRoot = appConfig.getConfigType();

    // Grap the proper section of the JSON
    JSONObject jResponse = new JSONObject(sResponse);
    JSONObject jApp = jResponse.getJSONObject(sJsonRoot);

    return parseAppInstance(jApp);
  }

  private static void parseAppBase(JSONObject jApp, AppModel appModel) {
    // Convert the JSON values to the AppModel
    appModel.setLanguage(jApp.getString("language"));
    appModel.setHealthStatus(jApp.getString("health_status"));
    
    Date dLastReportedAt = getLastReported(jApp);
    appModel.setLastReportedAt(dLastReportedAt);
  }

  public static AppModel parseAppOnly(JSONObject jApp) {
    AppModel appModel = new AppModel();
    parseAppBase(jApp, appModel);
    appModel.setAppId(jApp.getInt("id"));
    appModel.setAppName(jApp.getString("name"));
    appModel.setReporting(jApp.getBoolean("reporting"));
    return appModel;
  }

  public static AppHostModel parseAppHost(JSONObject jApp) {
    AppHostModel appHostModel = new AppHostModel();
    parseAppBase(jApp, appHostModel);
    appHostModel.setHostId(jApp.getInt("id"));
    appHostModel.setAppName(jApp.getString("application_name"));
    appHostModel.setHost(jApp.getString("host"));
    return appHostModel;
  }

  public static AppInstanceModel parseAppInstance(JSONObject jApp) {
    AppInstanceModel appInstanceModel = new AppInstanceModel();
    parseAppBase(jApp, appInstanceModel);
    appInstanceModel.setInstanceId(jApp.getInt("id"));
    appInstanceModel.setAppName(jApp.getString("application_name"));
    appInstanceModel.setHost(jApp.getString("host"));
    
    // Port is supplied by certain Agents like Java
    if (jApp.has("port")) {
      appInstanceModel.setPort(jApp.getInt("port"));
    }
    return appInstanceModel;
  }

  private static Date getLastReported(JSONObject jApp) {
    // Parse out the date
    try {
      if (jApp.has("last_reported_at")) {
        String sLastReportedAt = jApp.getString("last_reported_at");
        Date dLastReportedAt = df.parse(sLastReportedAt);
        return dLastReportedAt;
      }
    } catch (ParseException pe) {
      log.error(pe.getMessage(), pe);
    }
    return null;
  }
}