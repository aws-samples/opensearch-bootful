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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * @author Angel Conde
 */
@Slf4j
@Configuration
public class AWSCredentialsConfig {

  @Value("${aws.iam.role}")
  private String assumeRoleARN = "";

  @Value("${aws.os.region}")
  private String region;

  @Bean
  public StsAssumeRoleCredentialsProvider customPermissionProvider() {
    log.info("Assuming role " + assumeRoleARN);
    StsClient stsClient = StsClient.builder().region(Region.of(region)).build();

    StsAssumeRoleCredentialsProvider creds = assumeGivenRole(stsClient, assumeRoleARN, "bootful");

    return creds;
  }

  private StsAssumeRoleCredentialsProvider assumeGivenRole(
      StsClient stsClient, String roleArn, String roleSessionName) {
    AssumeRoleRequest roleRequest =
        AssumeRoleRequest.builder().roleArn(roleArn).roleSessionName(roleSessionName).build();

    AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);

    return StsAssumeRoleCredentialsProvider.builder()
        .stsClient(stsClient)
        .refreshRequest(roleRequest)
        .build();

  }
}
