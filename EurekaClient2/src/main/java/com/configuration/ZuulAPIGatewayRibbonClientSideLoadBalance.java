package com.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ZuulAPIGatewayRibbonClientSideLoadBalance {

	@Value("${server.port}")
	private String port;
	
	@GetMapping("/loadBalance")
	public String clientSideLoadBalacing() {
		return "client-Side LoadBalacing Testing using Nextflix Ribbon. This Service "
			  +" running on port -- " +port;
	}
}
