package goormthon.team28.startup_valley.security.config;

import goormthon.team28.startup_valley.security.filter.JwtAuthenticationFilter;
import goormthon.team28.startup_valley.security.filter.JwtExceptionFilter;
import goormthon.team28.startup_valley.security.handler.exception.CustomAccessDeniedHandler;
import goormthon.team28.startup_valley.security.handler.exception.CustomAuthenticationEntryPointHandler;
import goormthon.team28.startup_valley.security.handler.login.DefaultFailureHandler;
import goormthon.team28.startup_valley.security.handler.login.DefaultSuccessHandler;
import goormthon.team28.startup_valley.security.handler.logout.CustomLogoutProcessHandler;
import goormthon.team28.startup_valley.security.handler.logout.CustomLogoutResultHandler;
import goormthon.team28.startup_valley.security.provider.JwtAuthenticationManager;
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
    private final DefaultSuccessHandler defaultSuccessHandler;
    private final DefaultFailureHandler defaultFailureHandler;
    private final CustomLogoutProcessHandler customLogoutProcessHandler;
    private final CustomLogoutResultHandler customLogoutResultHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationManager jwtAuthenticationManager;
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
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/users/**").hasAnyRole("USER")
                                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/api/v1/auth/sign-in")
                        .usernameParameter("serial_id")
                        .passwordParameter("password")
                        .successHandler(defaultSuccessHandler)
                        .failureHandler(defaultFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
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