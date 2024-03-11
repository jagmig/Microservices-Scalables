package com.jagmi.products_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf-> csrf.disable())
                .securityMatcher("/**").authorizeHttpRequests((authorizeHttpRequest)->
                        authorizeHttpRequest.anyRequest().authenticated())  //Definimos que todas las peticiones deben estar autenticadas
                .oauth2ResourceServer((configure)-> configure.jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthConverter())) //Habilitamos el servidor de recursos oauth2
                        );  //El método jwtAuthenticationConverter devuelve una instancia de jwtAuthenticationConverter personalizada
                            //Lo que permite adaptar la extraccion y el mapeo de la informacion del token jwt segun las necesidades especificas.


        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("http://localhost:9090/realms/master");
    }

    /**
     * Estos métodos siguientes son para convertir los roles de Keycloak en una representación compatible con Spring Security
     * @return
     */
    private Converter<Jwt,? extends AbstractAuthenticationToken> jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeyCloackRealmRoleConverter());

        return converter;
    }
}

/**
 * Esta clase implementa la Interfaz Converter y se encarga de convertir los roles de KeyCloack en instancias de
 * GrantedAuthority que es la interfaz utilizada por Spring Security para representar autoridades o roles de seguridad.
 */
@SuppressWarnings("unchecked")
class KeyCloackRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>>{

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
       if(jwt.getClaims()==null) {
           return List.of();
       }
       final Map<String, List<String>> realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");

       return realmAccess.get("roles").stream()
               .map(roleName -> "ROLE_" + roleName)
               .map(SimpleGrantedAuthority::new)
               .collect(Collectors.toList());
    }
}
