package com.example.kirby;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
public class KirbyApplication {

	/*
	 * @Value("${http.port}") private Integer port;
	 */

	public static void main(String[] args) {
		SpringApplication.run(KirbyApplication.class, args);
	}

	/*
	 * @Bean public ServletWebServerFactory servletContainer() {
	 * TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
	 * tomcat.addAdditionalTomcatConnectors(createStandardConnector()); return
	 * tomcat; }
	 * 
	 * private Connector createStandardConnector() { Connector connector = new
	 * Connector("org.apache.coyote.http11.Http11NioProtocol");
	 * connector.setPort(port); return connector; }
	 */

	@Bean
	MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name}") String applicationName) {
		return (registry) -> registry.config().commonTags("application", applicationName);
	}
}
