package com.conficlient.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/test")
public class TestConfigClientController {

	@Autowired
	Configuration myConfig;
	
	 @Value("${msg:Hello world - Config Server is not working..pelase check}")
	 private String msg;

	@GetMapping(path="/config")
	public String getConfigFromGit() {
		
		return this.msg + this.myConfig.getName();
	}

}
