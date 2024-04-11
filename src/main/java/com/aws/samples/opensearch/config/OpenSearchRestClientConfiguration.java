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

import io.github.acm19.aws.interceptor.http.AwsRequestSigningApacheInterceptor;
import io.github.acm19.aws.interceptor.http.AwsRequestSigningApacheV5Interceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequestInterceptor;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

/**
 * @author Angel Conde
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.aws.samples.opensearch.repository")
@Slf4j
public class OpenSearchRestClientConfiguration extends AbstractOpenSearchConfiguration {

  @Value("${aws.os.endpoint}")
  private String endpoint = "";

  @Value("${aws.os.region}")
  private String region = "";

  private StsAssumeRoleCredentialsProvider credentialsProvider = null;

  @Autowired
  public OpenSearchRestClientConfiguration(StsAssumeRoleCredentialsProvider provider) {
    credentialsProvider = provider;
  }

  /**
   * SpringDataOpenSearch data provides us the flexibility to implement our custom {@link
   * RestHighLevelClient} instance by implementing the abstract method {@link
   * AbstractOpenSearchConfiguration#opensearchClient()},
   *
   * @return RestHighLevelClient. Amazon OpenSearch Service Https rest calls have to be signed with
   *     AWS credentials, hence an interceptor {@link HttpRequestInterceptor} is required to sign
   *     every API calls with credentials. The signing is happening through the below snippet <code>
   * signer.sign(signableRequest, awsCredentialsProvider.getCredentials());
   * </code>
   */
  @Override
  @Bean
  public RestHighLevelClient opensearchClient() {
    Aws4Signer signer = Aws4Signer.create();
    Aws4SignerParams signerParams =
        Aws4SignerParams.builder()
            .signingRegion(Region.of(region))
            .awsCredentials(credentialsProvider.resolveCredentials())
            .signingName("es")
            .build();
    HttpRequestInterceptor interceptor = new AwsRequestSigningApacheInterceptor(
            "es",
            signer,
            credentialsProvider,
            Region.of(region)
    );


    final ClientConfiguration clientConfiguration =
        ClientConfiguration.builder()
            .connectedTo(endpoint)
            .usingSsl()
            .withConnectTimeout(Duration.ofSeconds(10))
            .withSocketTimeout(Duration.ofSeconds(5))
            .withClientConfigurer(
                RestClients.RestClientConfigurationCallback.from(
                    httpAsyncClientBuilder -> {
                      httpAsyncClientBuilder.addInterceptorLast(interceptor);
                      return httpAsyncClientBuilder;
                    }))
            .build();
    return RestClients.create(clientConfiguration).rest();
  }
}
