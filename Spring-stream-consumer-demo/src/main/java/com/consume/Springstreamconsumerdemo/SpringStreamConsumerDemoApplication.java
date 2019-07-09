package com.consume.Springstreamconsumerdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@SpringBootApplication
@EnableBinding(Sink.class)
public class SpringStreamConsumerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringStreamConsumerDemoApplication.class, args);
	}

	@StreamListener(target = Sink.INPUT)
	public void processRegisterEmployees(String employee) {
		System.out.println("Employees Registered by Client " + employee);
	}
}
