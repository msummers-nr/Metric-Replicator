package com.nrh.api.module.nr.client.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ParserToApp {
  
  private static final Logger log = LoggerFactory.getLogger(ParserToApp.class);
  
  // Example date: 2018-01-02T19:30:00+00:00
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
  private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
  
  public static ArrayList<AppModel> strToAppList (String sResponse, AppConfig appConfig) throws IOException {
    
    // The configType is "application" or "application_host", etc. must add "s" for list call
    String sJsonRoot = appConfig.getConfigType() + "s";

    // Get the JSON format
    ArrayList<AppModel> appList = new ArrayList<>();
    JSONObject jResponse = new JSONObject(sResponse);
    JSONArray jAppList = jResponse.getJSONArray(sJsonRoot);
    
    // Loop over the applications
    for (int i = 0; i < jAppList.length(); i++) {
      JSONObject jApp = jAppList.getJSONObject(i);
      AppModel app = jsonToAppModel(jApp, appConfig);
      appList.add(app);
    }
    return appList;
  }

  public static AppModel strToAppModel (String sResponse, AppConfig appConfig) throws IOException {
    
    // The configType is "application" or "application_host" or "application_instance"
    String sJsonRoot = appConfig.getConfigType();

    // Grap the proper section of the JSON
    JSONObject jResponse = new JSONObject(sResponse);
    JSONObject jApp = jResponse.getJSONObject(sJsonRoot);

    return jsonToAppModel(jApp, appConfig);
  }

  private static AppModel jsonToAppModel(JSONObject jApp, AppConfig appConfig) throws IOException {
    
    String configType = appConfig.getConfigType();
    if (configType.equals(AppConfig.TYPE_APP_ONLY)) {
      return parseApplicationOnly(jApp);
    }
    if (configType.equals(AppConfig.TYPE_APP_HOST)) {
      return parseApplicationHost(jApp);
    }
    if (configType.equals(AppConfig.TYPE_APP_INSTANCE)) {
      return parseApplicationInstance(jApp);
    }

    // If we reach here then it's an error
    throw new IOException("Unsupported configType: " + configType);
  }

  private static void parseApplicationBase(JSONObject jApp, AppModel appModel) {
    // Convert the JSON values to the AppModel
    appModel.setLanguage(jApp.getString("language"));
    appModel.setHealthStatus(jApp.getString("health_status"));
    
    Date dLastReportedAt = getLastReported(jApp);
    appModel.setLastReportedAt(dLastReportedAt);
  }

  private static AppModel parseApplicationOnly(JSONObject jApp) {
    AppModel appModel = new AppModel();
    parseApplicationBase(jApp, appModel);
    appModel.setAppId(jApp.getInt("id"));
    appModel.setAppName(jApp.getString("name"));
    appModel.setReporting(jApp.getBoolean("reporting"));
    return appModel;
  }

  private static AppHostModel parseApplicationHost(JSONObject jApp) {
    AppHostModel appHostModel = new AppHostModel();
    parseApplicationBase(jApp, appHostModel);
    appHostModel.setHostId(jApp.getInt("id"));
    appHostModel.setAppName(jApp.getString("application_name"));
    appHostModel.setHost(jApp.getString("host"));
    return appHostModel;
  }

  private static AppHostModel parseApplicationInstance(JSONObject jApp) {
    AppInstanceModel appInstanceModel = new AppInstanceModel();
    parseApplicationBase(jApp, appInstanceModel);
    appInstanceModel.setAppId(jApp.getInt("id"));
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