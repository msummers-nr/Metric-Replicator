package com.nrh.api.module.nr.client.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nrh.api.module.nr.config.*;
import com.nrh.api.module.nr.model.*;
import java.io.IOException;
import java.util.ArrayList;

public class ParseAppList {
  
  private static final Logger log = LoggerFactory.getLogger(ParseAppList.class);
  
  public static ArrayList<AppModel> strToAppList (String sResponse, AppConfig appConfig) throws IOException {
    
    JSONArray jAppList = getAppList(sResponse, appConfig);
    log.debug("strToAppList length: " + jAppList.length());
    
    // Loop over the applications
    ArrayList<AppModel> appList = new ArrayList<>();
    for (int i = 0; i < jAppList.length(); i++) {
      JSONObject jApp = jAppList.getJSONObject(i);
      AppModel app = ParseAppShow.parseAppOnly(jApp);
      appList.add(app);
    }
    return appList;
  }

  public static ArrayList<AppHostModel> strToAppHostList (String sResponse, AppConfig appConfig) throws IOException {
    
    JSONArray jAppList = getAppList(sResponse, appConfig);
    log.debug("strToAppList length: " + jAppList.length());

    // Loop over the applications
    ArrayList<AppHostModel> appList = new ArrayList<>();
    for (int i = 0; i < jAppList.length(); i++) {
      JSONObject jApp = jAppList.getJSONObject(i);
      AppHostModel app = ParseAppShow.parseAppHost(jApp);
      appList.add(app);
    }
    return appList;
  }

  public static ArrayList<AppInstanceModel> strToAppInstanceList (String sResponse, AppConfig appConfig) throws IOException {
    
    JSONArray jAppList = getAppList(sResponse, appConfig);
    log.debug("strToAppList length: " + jAppList.length());

    // Loop over the applications
    ArrayList<AppInstanceModel> appList = new ArrayList<>();
    for (int i = 0; i < jAppList.length(); i++) {
      JSONObject jApp = jAppList.getJSONObject(i);
      AppInstanceModel app = ParseAppShow.parseAppInstance(jApp);
      appList.add(app);
    }
    return appList;
  }

  private static JSONArray getAppList(String sResponse, AppConfig appConfig) {
    // The configType is "application" or "application_host", etc. must add "s" for list call
    String sJsonRoot = appConfig.getConfigType() + "s";

    // Get the JSON format
    JSONObject jResponse = new JSONObject(sResponse);
    JSONArray jAppList = jResponse.getJSONArray(sJsonRoot);
    return jAppList;
  }
}