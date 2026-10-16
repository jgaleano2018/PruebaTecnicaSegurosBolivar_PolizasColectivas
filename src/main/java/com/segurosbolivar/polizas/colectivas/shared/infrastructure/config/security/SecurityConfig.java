package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.security;

import com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties.SecurityProperties;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.filter.JsonErrorWriter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.util.StringUtils;

/**
 * Seguridad: OAuth2 Resource Server (JWT Bearer emitido por Keycloak) + header x-api-key (ver ApiKeyWebFilter).
 * Lectura: roles POLIZAS_CONSULTA o POLIZAS_ADMIN. Escritura: POLIZAS_ADMIN.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    public static final String ROL_CONSULTA = "POLIZAS_CONSULTA";
    public static final String ROL_ADMIN = "POLIZAS_ADMIN";

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                  SecurityProperties properties,
                                                  JsonErrorWriter errorWriter,
                                                  ObjectProvider<ReactiveJwtDecoder> jwtDecoder) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, e) -> errorWriter.escribir(exchange,
                                HttpStatus.UNAUTHORIZED, "TOKEN_INVALIDO", "Token JWT ausente, expirado o inválido"))
                        .accessDeniedHandler((exchange, e) -> errorWriter.escribir(exchange,
                                HttpStatus.FORBIDDEN, "ACCESO_DENEGADO", "El usuario no tiene permisos para esta operación")));

        if (!properties.jwtEnabled()) {
            // Modo desarrollo: solo se exige x-api-key.
            http.authorizeExchange(auth -> auth.anyExchange().permitAll());
            return http.build();
        }

        List<String> sinJwt = new ArrayList<>(properties.publicPaths());
        sinJwt.addAll(properties.jwtExcludedPaths());
        sinJwt.add("/actuator/health/**");

        http.authorizeExchange(auth -> auth
                        .pathMatchers(sinJwt.toArray(String[]::new)).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/**").hasAnyRole(ROL_CONSULTA, ROL_ADMIN)
                        .anyExchange().hasRole(ROL_ADMIN))
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt
                        .jwtDecoder(jwtDecoder.getObject())
                        .jwtAuthenticationConverter(KeycloakJwtAuthoritiesConverter.reactivo())));
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security", name = "jwt-enabled", havingValue = "true")
    ReactiveJwtDecoder reactiveJwtDecoder(SecurityProperties properties) {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(properties.jwkSetUri()).build();
        OAuth2TokenValidator<Jwt> validador = StringUtils.hasText(properties.issuerUri())
                ? JwtValidators.createDefaultWithIssuer(properties.issuerUri())
                : JwtValidators.createDefault();
        decoder.setJwtValidator(validador);
        return decoder;
    }
}
