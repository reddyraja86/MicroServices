package com.basicauth.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class APIDetailsController {

	@GetMapping("/test")
	public String test() {
		return "Authenticated fot this rest API";
	}
}
