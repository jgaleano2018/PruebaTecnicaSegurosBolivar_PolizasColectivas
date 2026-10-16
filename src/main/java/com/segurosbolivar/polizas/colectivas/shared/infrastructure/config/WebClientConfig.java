package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config;

import com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties.CoreClientProperties;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties.SecurityProperties;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.filter.CorrelationIdWebFilter;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * WebClient hacia el CORE: timeouts, x-api-key, propagación del JWT del usuario (token relay)
 * y del X-Correlation-Id para trazabilidad extremo a extremo.
 */
@Configuration
public class WebClientConfig {

    @Bean
    WebClient coreWebClient(WebClient.Builder builder, CoreClientProperties core, SecurityProperties security) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) core.connectTimeout().toMillis())
                .responseTimeout(core.responseTimeout());
        return builder.baseUrl(core.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(security.apiKeyHeader(), core.apiKey())
                .filter(tokenRelay())
                .filter(correlationId())
                .build();
    }

    private static ExchangeFilterFunction tokenRelay() {
        return (request, next) -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(AbstractOAuth2TokenAuthenticationToken.class::isInstance)
                .map(auth -> ((AbstractOAuth2TokenAuthenticationToken<?>) auth).getToken().getTokenValue())
                .map(token -> ClientRequest.from(request).headers(h -> h.setBearerAuth(token)).build())
                .defaultIfEmpty(request)
                .flatMap(next::exchange);
    }

    private static ExchangeFilterFunction correlationId() {
        return (request, next) -> Mono.deferContextual(ctx -> {
            ClientRequest conId = ctx.hasKey(CorrelationIdWebFilter.ATRIBUTO)
                    ? ClientRequest.from(request)
                            .header(CorrelationIdWebFilter.HEADER, ctx.get(CorrelationIdWebFilter.ATRIBUTO).toString()).build()
                    : request;
            return next.exchange(conId);
        });
    }
}
