package com.springmango.repositories;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.springmango.entities.Person;

@Repository
public interface PersonRepository extends  MongoRepository<Person, BigInteger>{
	

}
