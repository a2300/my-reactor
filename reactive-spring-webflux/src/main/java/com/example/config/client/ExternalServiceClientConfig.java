package com.example.config.client;

import com.example.client.ExternalClient;
import com.example.config.properties.ExternalServiceClientProperties;
import com.example.exception.RetryExhaustedException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(ExternalServiceClientProperties.class)
public class ExternalServiceClientConfig {
    @Bean
    public ExternalClient exchangeExternalClient(ExternalServiceClientProperties clientProperties,
                                                         WebClient.Builder webClientBuilder) {

        var httpClient = httpClient(clientProperties);
        var connector = new ReactorClientHttpConnector(httpClient);

        var webClient = webClientBuilder
                .baseUrl(clientProperties.url())
                .clientConnector(connector)
                .build();

        var factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();
        return factory.createClient(ExternalClient.class);
    }

    @Bean
    public RetryBackoffSpec retryBackoffSpec(ExternalServiceClientProperties clientProperties) {
        var webClientProperties = clientProperties.webClient();

        return Retry.backoff(
                        webClientProperties.maxRetries(),
                        Duration.ofMillis(webClientProperties.minBackoffMillis())
                )
                .filter(throwable -> !is4xxClientError(throwable))
                .onRetryExhaustedThrow((spec, signal) -> {
                    if (signal.failure() != null) {
                        return new RetryExhaustedException(signal.totalRetries(), signal.failure());
                    }
                    return new RetryExhaustedException(signal.totalRetries());
                });
    }

    private boolean is4xxClientError(Throwable throwable) {
        return throwable instanceof WebClientResponseException exception
                && exception.getStatusCode().is4xxClientError();
    }

    private HttpClient httpClient(ExternalServiceClientProperties clientProperties) {
        var webClientProperties = clientProperties.webClient();
        return HttpClient
                .create()
                .baseUrl(clientProperties.url())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperties.connectTimeoutMillis())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(webClientProperties.readTimeoutMillis(), TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(webClientProperties.writeTimeoutMillis(), TimeUnit.MILLISECONDS));
                });
    }

}
