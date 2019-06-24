package com.springredis.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springredis.entity.Person;

public interface PersonRespository extends JpaRepository<Person, BigInteger>{

	
}
