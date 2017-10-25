# New Relic API Client (Java) [![Build Status](https://travis-ci.org/kenahrens/newrelic-api-client-java.svg?branch=master)](https://travis-ci.org/kenahrens/newrelic-api-client-java)

Java library for interfacing with New Relic APIs

# Setting up your credentials
There is a template config file in the config directory, it is in [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) format. You can make a copy of this file for your own configuration. The config uses different identifiers, this section of the documentation covers [API Keys](https://docs.newrelic.com/docs/apis/rest-api-v2/getting-started/api-keys) and how they are used.

| Type | Name | Description |
|---|---|---|
| Label | accountName | Just a label to help know the name of the account, used in logging. |
| Label | adminName | A label to know who is the owner of the Admin API Key. |
| ID | accountId | The [Account ID](https://docs.newrelic.com/docs/accounts-partnerships/accounts/account-setup/account-id) is required to query data from your account. |
| API Key | licenseKey | The [License Key](https://docs.newrelic.com/docs/accounts-partnerships/accounts/account-setup/license-key) is used for Agents or Plugins to send data to your account. |
| API Key | restKey | |
| API Key | adminKey | |
| API Key | insightsQueryKey | |
| API Key | insightsInsertKey | |

## Labels and IDs
* Account Name - This is just a label you can supply, helps in logging
* Admin Name - This is also a label to know who the Admin API Key belongs to
*  - This is the id used in the URL bar and identifies your account

## API Keys

* License Key - This is used by Agents and Plugins to send data to New Relic
* REST API Key - This 

## Main REST API
You can see the list of calls on the [API Explorer](https://api.newrelic.com/).

### Supported
* Applications (list, show, metricNames metricData)

### To Do
* Application Hosts
* Application Instances
* Application Deployments
* Mobile Applications
* Browser Applications
* Key Transactions
* Usages
* Alert Events
* Alert Conditions
* Alert Plugin Conditions
* Alert External Service Conditions
* Alerts Synthetics Conditions
* Alerts NRQL Conditions
* Alerts Policies
* Alerts Channels
* Alerts Policy Channels
* Alerts Violations
* Alerts Incidents
* Alerts Entity Conditions
* Plugins
* Components
* Labels

### Deprecated / Will Not Support
* Servers
* Legacy Alert Policies
* Legacy Notification Channels

## Insights API
You can see the details in the [Insights API Docs](https://docs.newrelic.com/docs/insights/insights-api).

### Supported
* Query API
* Insert API

## Synthetics API
TBD

## Plugins API
TBD