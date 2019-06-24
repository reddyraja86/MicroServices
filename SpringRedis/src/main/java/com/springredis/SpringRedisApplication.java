package com.springredis;

import java.math.BigInteger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.springredis.entity.Person;
import com.springredis.repository.PersonRespository;

@SpringBootApplication
@EnableCaching
public class SpringRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRedisApplication.class, args);
	}

	@Bean
	public CommandLineRunner saveData(PersonRespository personRepository) {

		Person p = new Person();
		p.setId(new BigInteger("1"));
		p.setName("Raju");
		p.setAddress("Hyderabad");

		Person p1 = new Person();
		p1.setId(new BigInteger("2"));
		p1.setName("Anjali");
		p1.setAddress("Vizag");


		return args -> {
			personRepository.save(p);
			personRepository.save(p1);

		};
	}

}
