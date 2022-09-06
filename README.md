# Amazon OpenSearch Spring Boot using Spring Data & High Level Rest Client

Connecting to Amazon OpenSearch using Spring Boot using Spring Data and High Level Rest client.

Amazon OpenSearch Https rest calls have to be signed with AWS credentials that will be mapped to a IAMRole with a custom permissions model mapped inside OpenSearch, hence an interceptor is required to sign every API calls with credentials ([docs](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/request-signing.html#request-signing-java)).


**This sample code does not secure the Rest API in any way.**

In a production environment you could also employ Amazon Cognito or
other mechanisms to secure the API and dynamically attach the role to the API depending the logged user following best practices.  The project could be integrated with [Spring Security](https://spring.io/projects/spring-security) in order to gain such features.


## Prerequisites

1. Create an Amazon OpenSearch cluster using this [guide](https://medium.com/@neuw84/securing-amazon-opensearch-service-dashboards-with-amazon-cognito-1f0b784cab3b).
2. Follow this blog post in order to setup the required IAM roles used by the app (**To-DO**)

Amazon OpenSearch provides the following endpoints

1. OpenSearch Cluster Rest endpoint
2. OpenSearch Dashboards endpoint

## Config

Provide the following configuration in the ```application.yaml``` file

1. ```aws.os.region=aws-region-you-are-using```
2. ```aws.os.endpoint=opensearch-domain-endpoint```
3. ```aws.iamrole=iam-master-role-ARN```

This project uses Master IAM Role as it is creating a new index with Fake data. The project is using STS to get
credentials for that Role, remember to adapt it for your needs. In the blogpost, the app is
launched via Fargate and the Task Role is used for the permission chain.

There are 2 resource flows in this project

1. CRUD Operation using ElasticsearchRepository - CustomerController

- To create,update,delete,retrieve data from the indexes
- The retrieve operation is limited

You can use [Postman](https://www.postman.com/) to interact with the API. The API docs can be seen at http://localhost:8080/swagger-ui/index.html

2. Live demo with URL: http://localhost:8080/search

- Start to input some characters in the search box, which will open an auto-complete box of maximum 5 suggestions.
- Complete the search text and click search button to see the search results.

## Notes:

The project will be updated when **spring-data-opensearch** is available. As by now it uses the latest AWS
recommended version of Elastic library ([docs](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/samplecode.html#client-compatibility)).

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.
