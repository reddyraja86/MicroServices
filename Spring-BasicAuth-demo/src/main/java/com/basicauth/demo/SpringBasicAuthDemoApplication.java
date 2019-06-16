package com.basicauth.demo;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class SpringBasicAuthDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBasicAuthDemoApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(UserRepository repo) {
		UserAccount userAccount = new UserAccount();
		userAccount.setUserName("Raja");
		userAccount.setPassWord("{noop}Raja");
		userAccount.setActive(true);
		System.out.println("CommandLineRunner");
		return 	args -> repo.save(userAccount);
	}
}

@Service
class UsersService  implements org.springframework.security.core.userdetails.UserDetailsService{

	@Autowired
	UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		System.out.println( userRepo.findByUserName(userName));
		
		
		return userRepo.findByUserName(userName).
				map(u -> new User(u.getUserName(), u.getPassWord(),u.isActive() , u.isActive(), u.isActive(), u.isActive(), 
				AuthorityUtils.createAuthorityList("ADMIN","USER"))).
				orElseThrow(()->new UsernameNotFoundException("UserName"+userName+"-- Not Found"));
	}
	
}

@Repository
interface UserRepository extends JpaRepository<UserAccount, Integer>{
	
	public Optional<UserAccount> findByUserName(String userName);
	
}

@Entity
class UserAccount {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String userName;
	private String passWord;
	private boolean active;
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
}