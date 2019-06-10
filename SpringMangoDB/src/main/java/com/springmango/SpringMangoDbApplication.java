package com.springmango;

import java.math.BigInteger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.springmango.entities.Person;
import com.springmango.repositories.PersonRepository;

@SpringBootApplication
public class SpringMangoDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMangoDbApplication.class, args);
	}

	
	@Bean
	public CommandLineRunner saveData(PersonRepository personRepository ) {
		
		Person p1 = new Person();
		p1.setId(BigInteger.valueOf(1));
		p1.setName("JOHN");
		p1.setAddress("HYDERABAD");
	
		return args -> {
			personRepository.save(p1);
			
		};
	}
}
