# MicroServices
Microservices is an echo system where we need to follow certain priciples.  
Design Patterns has to be implemented :  
1)Configuration  
2)Discovery Services  
3)API gateway  
4)Caching  
5)Logging  
6)data sharing between micro services  
	CQRS & SAGA design patterns

Load balancing  
Service discovery  
Health checks  
Authentication  
Traffic management and routing  
Circuit breaking and failover policy  
Security  
Metrics and telemetry  
Fault injection  
### Need to identify the service mesh and side car design patterns.  
### Need to complete the Caching with Redis 
### Need to complete Oauth2

## 1) Configuration Management  

* Reading properties using <b> @ConfigurationProperties</b>  
	We can use this annnotation on any configuration class and define properties for the variables defined in application.properties
file. 

	*Application.properties*  
	
		apartment.name=Sindhu Residency  
		apartment.owner= John  
		apartment.noofflats=20  
		apartment.location=Hyderabad  

	*Configuration class*  

		package com.basicauth.demo;

		import org.springframework.boot.context.properties.ConfigurationProperties;
		import org.springframework.stereotype.Component;

		@Component
		@ConfigurationProperties("apartment")
		public class Configuration {
			private String name;
			private String owner;
			
			private String noofflats;
			private String location;

		}

* Reading the properties by <b>@value</b>   

		@Value("${apartment.owner}")
		private String owner;


We can configure different properties files like  application-dev.properties,application-qa.properties,application-prod.properties  
based on the profile we setup different configurations will be picked up by spring application.


*  <b>Spring CloudConfigserver</b>  

	All the above we are not storing the configuration in central server and all the micro services need a central configuration to 	read these properties.

	Here the configuration is saved in Git repository and Configuration server will read this data from the git resposritory.
	All the clinet applications will be read the configuration by connecting to configuration server.
	
	*  <b>Implementing Cloud Config server</b>  
	
	Create spring boot configuration server which will read the properties from the GIT Repository.
	
	Need to add the config server dependency and Enable the  <b> @EnableDiscoveryClient @EnableConfigServer </b>

		@SpringBootApplication
		@EnableDiscoveryClient
		@EnableConfigServer
		public class ConfigServerDemoApplication {

			public static void main(String[] args) {
				SpringApplication.run(ConfigServerDemoApplication.class, args);
			}

		}
	
	Add the Git details in application.properties file,
	Note: Here the application name and properites file in GIT repo should be same
	
		spring.application.name=<git properties file name>
		server.port=8888
		spring.cloud.config.server.git.uri=https://github.com/reddyraja86/MicroServices.git
		spring.cloud.config.server.git.username=reddyraja86@gmail.com
		spring.cloud.config.server.git.password=<passowrd>
		spring.cloud.config.server.bootstrap=true

	*  <b>Implementing Cloud Config client</b>   
	
	This will read the properties from the config server, we need to provide the details of config server here.
	

		apartment.owner= John 
		apartment.noofflats=20  
		apartment.location=Hyderabad  
		spring.cloud.config.uri=http://localhost:8888
		spring.application.name=<name of the confg file in GIT>
		management.security.enabled=false
		
 
 
  
2) Discovery Server 
   Here the Eureka Server will act as a discovery server
   
   We need to add the Eureka Server Dependency in eureka server side and add @EnableEurekaServer annotation
   
   We need to add the following properties so that server won't discover itself.  
    
    server.port=8761  
    spring.application.name=eureka  
    eureka.client.register-with-eureka=false  
    eureka.client.fetch-registry=false  
    
   In client server which is registering to this Eureka Server should have Eureks Discovery dependency  
   
   In client properties file add the Eureka server specific details  
    spring.application.name=eureka-client  
    eureka.client.service-url.default-zone=http://localhost:8761/eureka/  
    eureka.instance.hostname=localhost  

## Spring Security :  
Spring will have   
Basic Security :  
 		Spring security module has the  org.springframework.security.core.userdetails.UserDetailsService interface which will allow to integrate  
with the other services for user authentication.  

@Service
class UsersService  implements org.springframework.security.core.userdetails.UserDetailsService{

	@Autowired
	UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		
		return userRepo.findByUserName(userName).
				map(u -> new User(u.getUserName(), u.getPassWord(),u.isActive() , u.isActive(), u.isActive(), u.isActive(), 
				AuthorityUtils.createAuthorityList("ADMIN","USER"))).
				orElseThrow(()->new UsernameNotFoundException("UserName"+userName+"-- Not Found"));
	}
	
}

In above we have integrated with the userRepository and get the userdetails.Once we got the user details from repository we will
return the spring security User object which is an implementatiton of User Details Service.  
		
