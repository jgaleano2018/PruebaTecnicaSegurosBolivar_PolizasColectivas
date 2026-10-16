package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import reactor.core.publisher.Mono;

/** Convierte los roles de Keycloak (realm_access.roles) y scopes en authorities de Spring. */
public final class KeycloakJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
            roles.forEach(rol -> authorities.add(new SimpleGrantedAuthority("ROLE_" + rol)));
        }
        Object scope = jwt.getClaims().get("scope");
        if (scope instanceof String scopes) {
            for (String s : scopes.split(" ")) {
                if (!s.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + s));
                }
            }
        }
        return authorities;
    }

    public static Converter<Jwt, Mono<AbstractAuthenticationToken>> reactivo() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtAuthoritiesConverter());
        converter.setPrincipalClaimName("preferred_username");
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
