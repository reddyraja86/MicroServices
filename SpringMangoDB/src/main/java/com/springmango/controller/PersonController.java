package com.springmango.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springmango.entities.Person;
import com.springmango.services.PersonService;

@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	PersonService personService;

	@PostMapping(path = "/create")
	public Person savePerson(@RequestBody Person person) {
		return personService.savePersonDetails(person);
	}

	@GetMapping(path = "/persons")
	public List<Person> getAllPersons() {
		return personService.getAllThePersons();
	}

	@GetMapping(path = "/test")
	public String test() {
		return "THIS IS TESTING";
	}

}
