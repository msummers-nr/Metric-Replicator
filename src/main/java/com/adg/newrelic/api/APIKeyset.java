package com.adg.newrelic.api;

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
	private String restKey;
	private String adminKey;
	private String insightsQueryKey;
	private String insightsInsertKey;
	
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