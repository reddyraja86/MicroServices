package com.configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ZuulAPIGatewayRibbonClientSideLoadBalance {
	
	private static final Logger LOG = Logger.getLogger(ZuulAPIGatewayRibbonClientSideLoadBalance.class.getName());

	@Value("${server.port}")
	private String port;
	
	@GetMapping("/loadBalance")
	public String clientSideLoadBalacing() {
		LOG.log(Level.INFO, "AT CLIENT SERVICE");
		return "client-Side LoadBalacing Testing using Nextflix Ribbon. This Service "
			  +" running on port -- " +port;
	}
}
