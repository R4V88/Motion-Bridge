package com.motionbridge.motionbridge.security.config;

import com.motionbridge.motionbridge.security.jwt.Http401UnathorizedEntryPoint;
import com.motionbridge.motionbridge.security.jwt.JwtConfig;
import com.motionbridge.motionbridge.security.jwt.JwtEmailAndPasswordAuthenticationFilter;
import com.motionbridge.motionbridge.security.jwt.JwtTokenVerifier;
import com.motionbridge.motionbridge.security.user.MotionbridgeUserDetailsService;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableConfigurationProperties({AdminConfig.class, JwtConfig.class})
public class MotionbridgeSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final MotionbridgeUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ManipulateUserDataUseCase userDataManipulationUseCase;
    private final Http401UnathorizedEntryPoint http401UnathorizedEntryPoint;
    private final AdminConfig config;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    @Bean
    User systemUser() {
        return config.adminUser();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/api/users/login",
                        "/api/register",
                        "/api/products/active")
                .permitAll()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/register/confirm*")
                .permitAll()
                .and()
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig, userDetailsService), JwtEmailAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest()
                .authenticated();

        http.exceptionHandling().authenticationEntryPoint(http401UnathorizedEntryPoint);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());

    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        MotionbridgeUserDetailsService detailsService = new MotionbridgeUserDetailsService(userDataManipulationUseCase, config);
        provider.setUserDetailsService(detailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
