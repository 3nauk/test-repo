package com.bbcron.myPackage;

import com.bnauk.bbcron.config.LoggingConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableAutoConfiguration
@Import(LoggingConfig.class)
@EnableConfigurationProperties
@SpringBootApplication
public class BBCronMyProjectMain {

  public static void main(String[] args) {

    SpringApplication.run(BBCronMyProjectMain.class, args);
  }

  @EnableWebSecurity
  @Configuration
  class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable().cors().configurationSource(corsConfigurationSource())
          .and()
          .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
          .authorizeRequests()
          .antMatchers("/bbcron/login", "/bbcron/authorize")
          .permitAll();
      //.anyRequest().authenticated();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(Arrays.asList("*"));
      configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
      configuration.setAllowCredentials(true);
      //the below three lines will add the relevant CORS response headers
      configuration.addAllowedOrigin("*");
      configuration.addAllowedHeader("*");
      configuration.addAllowedMethod("*");
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
    }
  }

  @Bean
  public OpenAPI customOpenAPI(@Value("${springdoc.description}") String appDesciption,
      @Value("${springdoc.version}") String appVersion) {
    return new OpenAPI().info(new Info()
        .title("BBCron.")
        .version(appVersion)
        .description(appDesciption)
        .termsOfService("http://swagger.io/terms/")
        .license(new License().name("Apache 2.0")
            .url("http://springdoc.org")));
  }

}
