package com.back;

import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


public class BaseTest {
    @Container
    public static final ElasticsearchContainer elasticsearchContainer
        = new ElasticsearchContainer(
        DockerImageName.parse("elasticsearch:nori")
    )
        .withEnv("discovery.type", "single-node")
        .withEnv("xpack.security.enabled", "false")
        .withExposedPorts(9200)
        .waitingFor(
                Wait.forHttp("/")
                        .forPort(9200)
                        .forStatusCode(200)
        );

    @DynamicPropertySource
    public static void setElasticsearchProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", () -> elasticsearchContainer.getHttpHostAddress());
    }


}
