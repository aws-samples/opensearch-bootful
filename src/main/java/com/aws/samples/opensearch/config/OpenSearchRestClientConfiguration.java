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

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.http.apache.client.impl.SdkHttpClient;
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

import java.time.Duration;

/**
 * @author Angel Conde
 *
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.aws.samples.opensearch.repository")
@Slf4j
public class OpenSearchRestClientConfiguration extends AbstractOpenSearchConfiguration {

    @Value("${aws.os.endpoint}")
    private String endpoint = "";

    @Value("${aws.os.region}")
    private String region = "";

    private AWSCredentialsProvider credentialsProvider = null;

    @Autowired
    public OpenSearchRestClientConfiguration(AWSCredentialsProvider provider){
        credentialsProvider=provider;
    }

    /**
     * SpringDataOpenSearch data provides us the flexibility to implement our custom {@link RestHighLevelClient} instance by implementing the abstract method {@link AbstractOpenSearchConfiguration#opensearchClient()},
     *
     * @return RestHighLevelClient. Amazon OpenSearch Service Https rest calls have to be signed with AWS credentials, hence an interceptor {@link HttpRequestInterceptor} is required to sign every
     * API calls with credentials. The signing is happening through the below snippet
     * <code>
     * signer.sign(signableRequest, awsCredentialsProvider.getCredentials());
     * </code>
     */



    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {
        AWS4Signer signer = new AWS4Signer();
        String serviceName = "es";
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(endpoint)
                .usingSsl()
                .withConnectTimeout(Duration.ofSeconds(10))
                .withSocketTimeout(Duration.ofSeconds(5))
                    .withClientConfigurer(RestClients.RestClientConfigurationCallback.from(httpAsyncClientBuilder ->{
                        httpAsyncClientBuilder.addInterceptorLast(interceptor);
                return httpAsyncClientBuilder;
                     }))
                .build();
        return RestClients.create(clientConfiguration).rest();


    }
}
