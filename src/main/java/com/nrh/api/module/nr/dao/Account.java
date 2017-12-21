package com.nrh.api.module.nr.dao;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account {

  @Id
  private int accountId;

  private String accountName;
	private String adminName;
	private String licenseKey;
	private String restKey;
	private String adminKey;
	private String insightsQueryKey;
  private String insightsInsertKey;
  
  protected Account() {}

  public Account(int accountId) {
    this.accountId = accountId;
  }

  public int getAccountId() {
    return accountId;
  }
  public String getAccountName() {
    return accountName;
  }
  public String getAdminKey() {
    return adminKey;
  }
  public String getAdminName() {
    return adminName;
  }
  public String getInsightsInsertKey() {
    return insightsInsertKey;
  }
  public String getInsightsQueryKey() {
    return insightsQueryKey;
  }
  public String getLicenseKey() {
    return licenseKey;
  }
  public String getRestKey() {
    return restKey;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }
  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }
  public void setAdminKey(String adminKey) {
    this.adminKey = adminKey;
  }
  public void setAdminName(String adminName) {
    this.adminName = adminName;
  }
  public void setInsightsInsertKey(String insightsInsertKey) {
    this.insightsInsertKey = insightsInsertKey;
  }
  public void setInsightsQueryKey(String insightsQueryKey) {
    this.insightsQueryKey = insightsQueryKey;
  }
  public void setLicenseKey(String licenseKey) {
    this.licenseKey = licenseKey;
  }
  public void setRestKey(String restKey) {
    this.restKey = restKey;
  }
  public String toString() {
    return accountName + "(" + accountId + ")";
  }
}