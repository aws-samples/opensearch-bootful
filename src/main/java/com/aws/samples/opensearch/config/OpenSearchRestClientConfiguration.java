package com.aws.samples.opensearch.config;

/*
 *        Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *        Permission is hereby granted, free of charge, to any person obtaining a copy of this
 *        software and associated documentation files (the "Software"), to deal in the Software
 *        without restriction, including without limitation the rights to use, copy, modify,
 *        merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *        permit persons to whom the Software is furnished to do so.
 *
 *        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *        INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *        PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *        HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.time.Duration;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.TransportOptions;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.opensearch.data.client.osc.OpenSearchConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import software.amazon.awssdk.http.SdkHttpClient;
import org.opensearch.client.RestClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
/**
 * @author Angel Conde
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.aws.samples.opensearch.repository")
@Slf4j
public class OpenSearchRestClientConfiguration extends OpenSearchConfiguration {

  @Value("${aws.os.endpoint}")
  private String endpoint = "";

  @Value("${aws.os.region}")
  private String region = "";

  private StsAssumeRoleCredentialsProvider credentialsProvider = null;

  @Autowired
  public OpenSearchRestClientConfiguration(StsAssumeRoleCredentialsProvider provider) {
    credentialsProvider = provider;

  }

  @NonNull
  @Override
  public ClientConfiguration clientConfiguration() {

    return ClientConfiguration.builder()
        .connectedTo(endpoint)
        .usingSsl()
        .withConnectTimeout(Duration.ofSeconds(10))
        .withSocketTimeout(Duration.ofSeconds(5))
        .build();
  }


  @Override
  public OpenSearchTransport opensearchTransport(RestClient restClient, JsonpMapper jsonpMapper) {
    SdkHttpClient httpClient = ApacheHttpClient.builder().build();
    return new AwsSdk2Transport(
            httpClient,
            endpoint,
            "es", // signing service name, use "aoss" for OpenSearch Serverless
            Region.of(region),
            AwsSdk2TransportOptions.builder().setCredentials(credentialsProvider).build()
    );
    }

  @Override
  public TransportOptions transportOptions() {
    return AwsSdk2TransportOptions.builder().setCredentials(credentialsProvider).build();
  }

}

