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
1) Configuration Management  

  Here the configuration is saved in Git repository and Configuration server will read this data from the gut resposritory.
  All the clinet applications will be read the configuration by connecting to configuration server.
  
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
		
