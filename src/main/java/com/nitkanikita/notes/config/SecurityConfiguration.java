package com.nitkanikita.notes.config;

import com.nitkanikita.notes.component.CookieJwtAuthenticationFilter;
import com.nitkanikita.notes.component.CookiesTokenConsumerOAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfiguration {

    private final CookiesTokenConsumerOAuth2SuccessHandler successHandler__NEW;
    private final CookieJwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 ->
                oauth2
                    .successHandler(successHandler__NEW)
                    .authorizationEndpoint(endpointConfig -> {
                        endpointConfig.baseUri("/auth/login");
                    })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}