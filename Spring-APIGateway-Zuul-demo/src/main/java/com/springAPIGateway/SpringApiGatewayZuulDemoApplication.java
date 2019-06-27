package com.springAPIGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class SpringApiGatewayZuulDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringApiGatewayZuulDemoApplication.class, args);
	}

}
