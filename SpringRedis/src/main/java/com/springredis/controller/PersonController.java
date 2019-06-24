package com.springredis.controller;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springredis.entity.Person;
import com.springredis.repository.PersonRespository;

@RestController
@RequestMapping(name = "/person")
public class PersonController {

	@Autowired
	PersonRespository personRepostiroty;

	@GetMapping(path = "/test1")
	public String testSaveData1() {
		return "test";
	}

	@GetMapping(path = "/save")
	public void testSaveData() {

		Person p = new Person();
		p.setId(new BigInteger("1"));
		p.setName("Raju");
		p.setAddress("Hyderabad");

		Person p1 = new Person();
		p1.setId(new BigInteger("2"));
		p1.setName("Anjali");
		p1.setAddress("Vizag");

		personRepostiroty.save(p);
		personRepostiroty.save(p1);

	}

	@Cacheable(value = "persons", key = "#personId")
	@GetMapping(path = "/persons/{personId}" )
	public Optional<Person> getPerson(@PathVariable String personId) {
		System.out.println("Getting person with ID {}."+ personId);
		Long p = Long.parseLong(personId);
		return personRepostiroty.findById(BigInteger.valueOf(p));
	}

}
