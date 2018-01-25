package com.nrh.api.module.nr.config;

import com.nrh.api.APIApplication;
import com.typesafe.config.Config;

/**
 * The API Keyset class holds a set of keys for a single account
 * 
 * @author kahrens
 *
 */
public class APIKeyset {
	private String accountName;
	private String adminName;
	private String accountId;
	private String licenseKey;
	private String restKey;
	private String adminKey;
	private String insightsQueryKey;
	private String insightsInsertKey;
	
	/**
	 * Populate 
	 * 
	 * @param conf the config for the whole API project
	 * @param accountName the name of the account to pull from the config
	 */
	public APIKeyset(Config conf, String account) {
		
		// Set all the local values from the config
		String prefix = "newrelic-api-client.accounts." + account;
		this.accountName = APIApplication.getConfString(prefix + ".accountName");
		this.adminName = APIApplication.getConfString(prefix + ".adminName");
		this.accountId = APIApplication.getConfString(prefix + ".accountId");
		this.licenseKey = APIApplication.getConfString(prefix + ".licenseKey");
		this.restKey = APIApplication.getConfString(prefix + ".restKey");
		this.adminKey = APIApplication.getConfString(prefix + ".adminKey");
		this.insightsQueryKey = APIApplication.getConfString(prefix + ".insightsQueryKey");
		this.insightsInsertKey = APIApplication.getConfString(prefix + ".insightsInsertKey");
	}
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getLicenseKey() {
		return licenseKey;
	}
	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}
	public String getRestKey() {
		return restKey;
	}
	public void setRestKey(String restKey) {
		this.restKey = restKey;
	}
	public String getAdminKey() {
		return adminKey;
	}
	public void setAdminKey(String adminKey) {
		this.adminKey = adminKey;
	}
	public String getInsightsQueryKey() {
		return insightsQueryKey;
	}
	public void setInsightsQueryKey(String insightsQueryKey) {
		this.insightsQueryKey = insightsQueryKey;
	}
	public String getInsightsInsertKey() {
		return insightsInsertKey;
	}
	public void setInsightsInsertKey(String insightsInsertKey) {
		this.insightsInsertKey = insightsInsertKey;
	}
}