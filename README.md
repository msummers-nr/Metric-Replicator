# New Relic API Client (Java) [![Build Status](https://travis-ci.org/kenahrens/newrelic-api-client-java.svg?branch=master)](https://travis-ci.org/kenahrens/newrelic-api-client-java)

Java library for interfacing with New Relic APIs

## Setting up your credentials
There is a template config file in the config directory, it is in [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) format. You can make a copy of this file for your own configuration. The config uses different identifiers, this section of the documentation covers [API Keys](https://docs.newrelic.com/docs/apis/rest-api-v2/getting-started/api-keys) and how they are used.

| Name | Description |
|---|---|
| accountName | Just a label to help know the name of the account, used in logging. |
| adminName | A label to know who is the owner of the Admin API Key. |
| accountId | The [Account ID](https://docs.newrelic.com/docs/accounts-partnerships/accounts/account-setup/account-id) is required to query data from your account. |
| licenseKey | The [License Key](https://docs.newrelic.com/docs/accounts-partnerships/accounts/account-setup/license-key) is used for Agents or Plugins to send data to your account. |
| restKey | The [Rest API Key](https://docs.newrelic.com/docs/apis/rest-api-v2/getting-started/api-keys) can be used to query certain kinds of data, it's recommended to use an Admin Key which provides more flexibility. |
| adminKey | The [Admin API Key](https://docs.newrelic.com/docs/apis/rest-api-v2/getting-started/api-keys) is like the Rest API Key but tied to a single admin user, there are also a few API calls that require this key. |
| insightsQueryKey | The [Insights Query API Key](https://docs.newrelic.com/docs/insights/insights-api/get-data/query-insights-event-data-api) is used to run NRQL queries and process the results. |
| insightsInsertKey |The [Insights Insert API Key](https://docs.newrelic.com/docs/insights/insights-data-sources/custom-data/insert-custom-events-insights-api) is used to publish custom events into Insights. |

## Using your custom config
Once you make a copy of config/template.conf to your own file, then you reference it with `-Dconfig.file` at runtime.

## Building and running the tests
This project relies on gradle, so you can test that everything is working properly (including your config file) like this:
```
$ ./gradlew -Dconfig.file=config/myconfig.conf test
:compileJava
:processResources UP-TO-DATE
:classes
:compileTestJava
:processTestResources UP-TO-DATE
:testClasses
:test
Hibernate: drop table account if exists
Hibernate: drop table application if exists

BUILD SUCCESSFUL

Total time: 6.39 secs
```

# API Details
There are multiple API systems involved, these tables have additional details.

## APIs Supported
| Type | Status | Description |
|---|---|---|
| Rest v2 API (not Alerts) | Partial | Query apps, metric names and data and POST to Plugins |
| Rest v2 API (Alerts) | Not supported yet | Broke out Alerts separately, it's a large set of APIs |
| Insights API | Partial | The query and insert calls are supported |
| Synthetics API | Not supported yet | CRUD functionality for Synthetic Monitors |

## APIs To Do
| Type | Description |
|---|---|
| Rest v2 API (not Alerts) | Need to add support for other endpoints (Mobile, Browser, Alerts, etc.) |
| Rest v2 API (Alerts) | Need all capabilities |
| Insights API | Needs an ability to potentially return results as DAO |
| Synthetics API | Need all capabilities |

## Deprecated / Will Not Support
* Servers
* Legacy Alert Policies
* Legacy Notification Channels
