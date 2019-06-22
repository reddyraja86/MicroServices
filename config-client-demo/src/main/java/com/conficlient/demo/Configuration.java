package com.conficlient.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("apartment")
public class Configuration {
	private String name;
	private String owner;

	private String noofflats;
	private String location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getNoofflats() {
		return noofflats;
	}

	public void setNoofflats(String noofflats) {
		this.noofflats = noofflats;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}