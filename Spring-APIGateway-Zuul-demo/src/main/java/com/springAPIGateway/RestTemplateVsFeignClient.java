package com.springAPIGateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



@RestController
@RequestMapping("/access")
public class RestTemplateVsFeignClient {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	TestFeignClient testFeignClient;
	
	@GetMapping("/restTemplate")
	public String clientSideLoadBalacing() {
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<String> response = this.restTemplate.exchange("http://eureka-client/client/loadBalance",
				HttpMethod.GET, new HttpEntity<>(headers), String.class);
		
		return response.getBody();
	}

	@GetMapping("/feignClient")
	public String clientSideLoadBalacingFeign() {
		System.out.println("---test");
		return testFeignClient.clientSideLoadBalacing();
	}
}
