package com.nrh.api.module.nr.client.rest;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.newrelic.api.agent.Trace;
import com.nrh.api.module.nr.client.Util;
import com.nrh.api.module.nr.config.APIKeyset;
import com.nrh.api.module.nr.config.AppConfig;
import com.nrh.api.module.nr.config.MetricConfig;
import com.nrh.api.module.nr.model.MetricDataModel;
import com.nrh.api.module.nr.model.MetricNameModel;

import io.netty.channel.ChannelOption;
import okhttp3.HttpUrl.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.ipc.netty.resources.PoolResources;

public abstract class AppBase {

	private static final Logger log = LoggerFactory.getLogger(AppBase.class);
	private static Scheduler scheduler = null;
	public static final String URL_HOST = "api.newrelic.com";
	private static WebClient webClient = null;

	// private static Scheduler getScheduler() {
	// if (scheduler == null) {
	// scheduler = Schedulers.newParallel("MetricProcessor", 20);
	// // scheduler = Schedulers.newElastic("MetricProcessor", 60);
	// }
	// return scheduler;
	// }

	private static WebClient getWebClient() {
		if (webClient == null) {
			log.info("getWebClient: create");
			log.info("getWebClient: poolSize: {}", poolSize);

			PoolResources poolResources = PoolResources.fixed("NIO-Pool", poolSize);
			ReactorClientHttpConnector connector = new ReactorClientHttpConnector(options -> options.poolResources(poolResources)
			      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000));
			webClient = WebClient.builder()
			      .baseUrl("http://" + URL_HOST)
			      .filter(logRequest())
			      .clientConnector(connector)
			      .build();
		}
		return webClient;
	}

	private static ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
			// clientRequest.headers()
			// .forEach((name, values) -> values.forEach(value -> log.debug("{}={}", name, value)));
			return Mono.just(clientRequest);
		});
	}

	private OkHttpClient client;
	private APIKeyset keys;

	public AppBase(APIKeyset keys, Boolean createClient) {
		this.keys = keys;
		if (createClient)
			client = new OkHttpClient.Builder().connectTimeout(0, TimeUnit.SECONDS)
			      .writeTimeout(0, TimeUnit.SECONDS)
			      .readTimeout(0, TimeUnit.SECONDS)
			      .build();
	}

	@Trace
	private String callAPI(Builder urlBuilder, String apiKey) throws IOException {
		log.debug("callAPI: enter");
		Request request = Util.createRequest(urlBuilder, apiKey);
		log.debug("callAPI: createRequest");
		Response response = Util.callSync(client, request);
		log.debug("callAPI: callSync");
		ResponseBody body = response.body();
		log.debug("callAPI: response.body");
		String sResponse = body.string();
		log.debug("callAPI: body.string: {}", sResponse.length());
		log.trace("callAPI: response: {}", sResponse);
		log.debug("callAPI: exit");
		return sResponse;
	}

	@Trace
	private Reader getAPIReader(Builder urlBuilder, String apiKey) throws IOException {
		log.debug("callAPI: enter");
		Request request = Util.createRequest(urlBuilder, apiKey);
		log.debug("callAPI: createRequest");
		Response response = Util.callSync(client, request);
		log.debug("callAPI: callSync");
		ResponseBody body = response.body();
		log.debug("callAPI: response.body");
		Reader reader = body.charStream();
		log.debug("callAPI: exit");
		return reader;
	}

	public String list(AppConfig appConfig, String segment) throws IOException {

		// Create the URL with the filters
		Builder urlBuilder = Util.startBuilder(URL_HOST, segment);
		Util.addFilters(urlBuilder, appConfig.getFilterMap());

		// Call the API for this URL
		String sResponse = callAPI(urlBuilder, keys.getRestKey());
		return sResponse;
	}

	public ArrayList<MetricDataModel> metricData(MetricConfig metricConfig, String segment) throws IOException {
		log.debug("metricData: enter");
		// Builder urlBuilder = Util.startBuilder(URL_HOST, segment);

		// Create the URL with the proper path and post value
		HttpUrl httpUrl = new HttpUrl.Builder()
			.scheme(Util.URL_SCHEME)
			.host(URL_HOST)
			.addPathSegments(segment)
			.build();
		
		FormBody.Builder formBody = new FormBody.Builder();

		log.debug("metricData: getMetricNameList");
		Collection<String> metricNameList = metricConfig.getMetricNameList();
		if (metricNameList != null) {
			for (String metricName : metricNameList) {
				formBody.add("names[]", metricName);
				// urlBuilder.addEncodedQueryParameter("names[]", metricName);
			}
		}
		
		// Make a POST request
		Request req = new Request.Builder()
			.url(httpUrl)
			.addHeader("X-Api-Key", keys.getRestKey())
			.post(formBody.build())
			.build();

		log.debug("metricData: url built");

		// Reader reader = getAPIReader(urlBuilder, keys.getRestKey());
		// String response = callAPI(urlBuilder, keys.getRestKey());
		Response rsp = Util.callSync(client, req);
		String response = rsp.body().string();
		log.debug("metricData: callAPI");

		metricConfig.setMetricType(MetricConfig.TYPE_METRIC_DATA);
		// ArrayList<MetricDataModel> result = ParserToMetric.fromReader(reader, metricConfig);
		ArrayList<MetricDataModel> result = ParserToMetric.strToMetricData(response, metricConfig);
		log.debug("metricData: exit");
		return result;
	}

	public Mono<List<MetricDataModel>> metricDataMono(MetricConfig metricConfig, String segment) {
		log.debug("metricDataMono: enter");
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(metricConfig.getMetricNameList()
		      .size());
		for (String value : metricConfig.getMetricNameList())
			queryParams.add("names[]", value);

		WebClient webClient = getWebClient();
		log.debug("metricDataMono: webClient: {}", webClient.toString());
		Mono<List<MetricDataModel>> mono = webClient.get()
		      .uri(builder -> builder.path(segment)
		            .queryParams(queryParams)
		            .scheme(Util.URL_SCHEME)
		            .build())
		      .accept(MediaType.APPLICATION_JSON)
		      .header("X-Api-Key", keys.getRestKey())
		      .retrieve()
		      .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
			      log.error("queryMetricData: is4xxClientError: {}", clientResponse.statusCode());
			      return Mono.error(new Exception());
		      })
		      .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
			      log.error("queryMetricData: is5xxServerError: {}", clientResponse.statusCode());
			      return Mono.error(new Exception());
		      })
		      // Everything after this happens on a nio thread :-)
		      .bodyToMono(String.class)
		      // .log()
		      // .publishOn(getScheduler())
		      .map(response -> {
			      // log.info("metricDataMono: map response");
			      metricConfig.setMetricType(MetricConfig.TYPE_METRIC_DATA);
			      List<MetricDataModel> result = ParserToMetric.fromString(response, metricConfig);
			      // List<MetricDataModel> result = ParserToMetric.strToMetricData(response, metricConfig);
			      return result;
		      });

		log.debug("metricDataMono: exit");
		return mono;
	}

	public ArrayList<MetricNameModel> metricNames(MetricConfig metricConfig, String segment) throws IOException {

		// Create the URL
		Builder urlBuilder = Util.startBuilder(URL_HOST, segment);

		// Add the paramter called name if it's defined
		if (metricConfig.getFilterName() != null) {
			urlBuilder.addEncodedQueryParameter("name", metricConfig.getFilterName());
		}

		// Call the API for this URL
		String sResponse = callAPI(urlBuilder, keys.getRestKey());

		// Parse the response correctly
		metricConfig.setMetricType(MetricConfig.TYPE_METRIC_NAME);
		return ParserToMetric.strToMetricNames(sResponse, metricConfig);
	}

	public String show(AppConfig appConfig, String segment) throws IOException {
		// Create the URL (no parameters)
		Builder urlBuilder = Util.startBuilder(URL_HOST, segment);

		// Call the API for this URL
		String sResponse = callAPI(urlBuilder, keys.getRestKey());
		return sResponse;
	}

	private static int poolSize;

	public static void setPoolSize(int size) {
		log.info("setPoolSize: {}", size);
		poolSize = size;
	}
}
