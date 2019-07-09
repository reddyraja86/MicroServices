package com.publisher.publisher;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EmployeeRegistrationSource {

	@Output("employeeRegistrationChannel")
	MessageChannel employeeRegistration();

}
