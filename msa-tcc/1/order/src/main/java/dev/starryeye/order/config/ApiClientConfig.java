package dev.starryeye.order.config;

import dev.starryeye.order.infrastructure.point.PointApiClient;
import dev.starryeye.order.infrastructure.product.ProductApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ApiClientConfig {

    @Bean
    public ProductApiClient productApiClient(ClientHttpRequestFactory jdkClientHttpRequestFactory) {
        return new ProductApiClient(
                RestClient.builder()
                        .requestFactory(jdkClientHttpRequestFactory)
                        .baseUrl("http://localhost:8081")
                        .build()
        );
    }

    @Bean
    public PointApiClient pointApiClient(ClientHttpRequestFactory jdkClientHttpRequestFactory) {
        return new PointApiClient(
                RestClient.builder()
                        .requestFactory(jdkClientHttpRequestFactory)
                        .baseUrl("http://localhost:8082/")
                        .build()
        );
    }

    @Bean
    public ClientHttpRequestFactory jdkClientHttpRequestFactory() {
        JdkClientHttpRequestFactory jdkClientHttpRequestFactory = new JdkClientHttpRequestFactory();
        jdkClientHttpRequestFactory.setReadTimeout(Duration.ofSeconds(2L));
        return jdkClientHttpRequestFactory;
    }
}
