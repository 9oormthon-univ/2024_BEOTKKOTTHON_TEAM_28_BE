package goormthon.team28.startup_valley.security.config;

import goormthon.team28.startup_valley.security.filter.JwtAuthenticationFilter;
import goormthon.team28.startup_valley.security.filter.JwtExceptionFilter;
import goormthon.team28.startup_valley.security.handler.exception.CustomAccessDeniedHandler;
import goormthon.team28.startup_valley.security.handler.exception.CustomAuthenticationEntryPointHandler;
import goormthon.team28.startup_valley.security.handler.login.DefaultFailureHandler;
import goormthon.team28.startup_valley.security.handler.login.DefaultSuccessHandler;
import goormthon.team28.startup_valley.security.handler.login.Oauth2FailureHandler;
import goormthon.team28.startup_valley.security.handler.login.Oauth2SuccessHandler;
import goormthon.team28.startup_valley.security.handler.logout.CustomLogoutProcessHandler;
import goormthon.team28.startup_valley.security.handler.logout.CustomLogoutResultHandler;
import goormthon.team28.startup_valley.security.provider.JwtAuthenticationManager;
import goormthon.team28.startup_valley.security.service.CustomOauth2UserDetailService;
import goormthon.team28.startup_valley.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final Oauth2FailureHandler oauth2FailureHandler;
    private final CustomOauth2UserDetailService customOauth2UserDetailService;
    private final CustomLogoutProcessHandler customLogoutProcessHandler;
    private final CustomLogoutResultHandler customLogoutResultHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/api/oauth2/sign-up").permitAll()
                                .requestMatchers("/api/**").hasAnyRole("USER")
                                .anyRequest().authenticated()
                )
                .oauth2Login(login -> login
                        .successHandler(oauth2SuccessHandler)
                        .failureHandler(oauth2FailureHandler)
                        .userInfoEndpoint(it -> it.userService(customOauth2UserDetailService))
                )
                .logout(logout -> logout
                        .logoutUrl("/api/users/sign-out")
                        .addLogoutHandler(customLogoutProcessHandler)
                        .logoutSuccessHandler(customLogoutResultHandler)
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPointHandler)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, jwtAuthenticationManager), LogoutFilter.class
                )
                .addFilterBefore(
                        new JwtExceptionFilter(), JwtAuthenticationFilter.class
                )
                .getOrBuild();
    }
}
