package com.springmango.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springmango.entities.Person;
import com.springmango.repositories.PersonRepository;


@Service
public class PersonService {

	@Autowired
	PersonRepository personRepository;
	
	public Person savePersonDetails(Person person) {
		return personRepository.save(person);
	}
	
	public List<Person> getAllThePersons(){
		return personRepository.findAll();
	}

}
