package com.tripsok_back.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticConfig {

	@Value("${ELASTIC_URL:http://localhost:9200}")
	private String elasticUrl;

	@Value("${ELASTIC_USERNAME:}")
	private String username;

	@Value("${ELASTIC_PASSWORD:}")
	private String password;

	@Bean
	public ElasticsearchClient elasticsearchClient() {
		RestClientBuilder builder = RestClient.builder(HttpHost.create(elasticUrl));

		List<Header> headers = new ArrayList<>();
		if (username != null && !username.isBlank()) {
			String basic = Base64.getEncoder().encodeToString((username + ":" + (password == null ? "" : password))
				.getBytes(StandardCharsets.UTF_8));
			headers.add(new BasicHeader("Authorization", "Basic " + basic));
		}
		if (!headers.isEmpty()) {
			builder.setDefaultHeaders(headers.toArray(Header[]::new));
		}

		RestClient restClient = builder.build();
		return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
	}
}
