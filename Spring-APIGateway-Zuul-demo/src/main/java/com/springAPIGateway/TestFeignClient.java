package com.springAPIGateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(decode404=true,name="eureka-client")
public interface TestFeignClient {

	@GetMapping("/client/loadBalance")
	public String clientSideLoadBalacing();
}
