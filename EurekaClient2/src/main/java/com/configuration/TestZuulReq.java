package com.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zuul")
public class TestZuulReq {

	@Value("${server.port}")
	private String port;
	
	@GetMapping("/zuulTest")
	public String zuulTest(@RequestHeader String TestZuulHeader) {
		return "Zuul Test "+TestZuulHeader+" running on port --" +port;
	}

}
