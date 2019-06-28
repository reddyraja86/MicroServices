package com.configuration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zuul")
public class TestZuulReq {

	
	@GetMapping("/zuulTest")
	public String zuulTest(@RequestHeader String TestZuulHeader) {
		return "Zuul Test "+TestZuulHeader;
	}

}
