# MicroServices
Microservices is an echo system where we need to follow certain priciples.  
Design Patterns has to be implemented :  
1)Configuration  
2)Discovery Services  
3)API gateway   
	3.1) Client Side LoadBalancing using Ribbon & API gateway   
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
##### Need to identify the service mesh and side car design patterns.  
##### Need to complete the Caching with Redis 
##### Need to complete Oauth2  
##### Need to implement config server & discovery with consul

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


	We can configure different properties files like  application-dev.properties,application-						qa.properties,application.prod.properties based on the profile we setup different configurations will be picked up by spring 		application.


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
		
 
## 2) Discovery Server     
  
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


## 3) API gateway

 Spring clous API gateway is the single point of entry in cloud environment.From API gateway other services will be communicated through the discovery service.  
Here the API gateay is zuul which will be registered with the eureka discovery service and all the other services also  registered to the discovery service.  

* User will give the request to API gateway. 
* API gateway will identify the respective service from discovery client and invoke the respective service.  
* Zuul proxy will take care of the client side load balancing using netflix ribbon. 
* Zuul will have filters which will be used to capture and modify the request or response.

* Note : <b> HERE client will make use of API gateway IP address and ports to invoke the services instead of microservice ip&port </b>

When Zuul receives a request, it picks up one of the physical locations available and forwards requests to the actual service instance. The whole process of caching the location of the service instances and forwarding the request to the actual location is provided out of the box with no additional configurations needed.  
Internally, Zuul uses Netflix Ribbon to look up for all instances of the service from the service discovery (Eureka Server).  

<b>Zuul Filters:</b>  

Zuul supports 4 types of filters namely pre,post,route and error. Here we will create each type of filters.

To write a filter we need to do basically these steps:

* Need to extend com.netflix.zuul.ZuulFilter  
* Need to override filterType, filterOrder, shouldFilter and run methods. Here filterType method can only return any one of four String – pre/post/route/error. Depedending on this value the filter will act like a particular filter.  
* run method is the place where our filter logic should be placed depending on our requirement.  
* Also we can add any number of any particular filter based on our need, this case filterOrder will come into place to determine the order of that filer at the phase of execution of that type of filter.  

* Configure zuul api gateway with @EnableZuulProxy annotation  

		package com.springAPIGateway;

		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
		import org.springframework.context.annotation.Bean;

		@SpringBootApplication
		@EnableZuulProxy
		public class SpringApiGatewayZuulDemoApplication {

			public static void main(String[] args) {
				SpringApplication.run(SpringApiGatewayZuulDemoApplication.class, args);
			}

			@Bean
			public PreFilter preFilter() {
				return new PreFilter();
			}

		}

*  Enable pre filter and add one header which will be read by one of the client service.

		package com.springAPIGateway;

		import javax.servlet.http.HttpServletRequest;

		import com.netflix.zuul.ZuulFilter;
		import com.netflix.zuul.context.RequestContext;
		import com.netflix.zuul.exception.ZuulException;

		public class PreFilter extends ZuulFilter {

			@Override
			public boolean shouldFilter() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public Object run() throws ZuulException {
				RequestContext ctx = RequestContext.getCurrentContext();
				HttpServletRequest httpReq = ctx.getRequest();
				ctx.addZuulRequestHeader("TestZuulHeader", " --- This is Header Value I set in Zuul API Gateway");
				System.out.println("--- We can manipulate the Request Here -------");
				return null;
			}

			@Override
			public String filterType() {
				// TODO Auto-generated method stub
				return "pre";
			}

			@Override
			public int filterOrder() {
				// TODO Auto-generated method stub
				return 0;
			}

		}

*  Regiater the API gateway with Discovery service

		server.port=8084
		spring.application.name=ZuulProxy
		eureka.client.service-url.default-zone=http://localhost:8761/eureka/
		eureka.instance.hostname=localhost
		

*  Create a client service to and register it with the Discovery server.Read the header set from the Zuul filter  
		
		package com.configuration;

		import org.springframework.web.bind.annotation.GetMapping;
		import org.springframework.web.bind.annotation.RequestHeader;
		import org.springframework.web.bind.annotation.RequestMapping;
		import org.springframework.web.bind.annotation.RestController;

		@RestController
		@RequestMapping("/zuul")
		public class TestZuulReq {


			@GetMapping("/zuulTest")
			public String zuulTest(@RequestHeader String TestZuulHeader) {
				return "Zuul Test "+TestZuulHeader;
			}

		}


*  Invoke the client service from Zuul API gateway ,which will invoke the pre filter and set the header values.Using the API gateway to host and service name invoke client service.

		http://localhost:8084/eureka-client/zuul/zuulTest

### 3.1) Client side LoadBalancing using Ribbon  & API gateway :  
	
*  Add the Netflix Ribbon Dependnecy to API gateway.   
*  Configure two client services and they will be running on different ports like 8081,8082.    
*  Crete a service which will return the port number of the client service.   

		package com.configuration;

		import org.springframework.beans.factory.annotation.Value;
		import org.springframework.web.bind.annotation.GetMapping;
		import org.springframework.web.bind.annotation.RequestMapping;
		import org.springframework.web.bind.annotation.RestController;

		@RestController
		@RequestMapping("/client")
		public class ZuulAPIGatewayRibbonClientSideLoadBalance {

			@Value("${server.port}")
			private String port;

			@GetMapping("/loadBalance")
			public String clientSideLoadBalacing() {
				return "client-Side LoadBalacing Testing using Nextflix Ribbon. This Service "
					  +" running on port -- " +port;
			}
			
 *  Now start two instance of client service and access the client service.  
 
 		http://localhost:8084/eureka-client/client/loadBalance

 * We can observe the service are load balanced as the port numnber will be different is returned.  
		}


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


## Spring Redis Cache :

This will be used reduce the number of network roundtrips between the database and user requests.Improves the performance by get the data from cache instead of data base.

We are using redis in memory cache for this.  



*  Now down load the radis server and start by double clicking on redis-server.exe  
*  Add the redis dependency for the spring boot application.  
*  Try to add the data in to 2 database using the commanline runner,which will be inserted once the applicatio started.
*  Add the redis server details in application.properties file

		spring.cache.type=redis
		spring.redis.host=localhost
		spring.redis.port=6379
		spring.jpa.show-sql=true
*  We need to use <b>@EnableCaching</b> annotation  

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


*  To insert some key value in cache we will use <b> @Cacheable(value = "persons", key = "#personId") </b>,here when we are getting the    data from databse we are inserting in redis cache.  
	
			@Cacheable(value = "persons", key = "#personId")
			@GetMapping(path = "/persons/{personId}" )
			public Optional<Person> getPerson(@PathVariable String personId) {
				System.out.println("Getting person with ID {}."+ personId);
				Long p = Long.parseLong(personId);
				return personRepostiroty.findById(BigInteger.valueOf(p));
			}
*  Here persons cache will be created and person object is stored against the personId key. (once we put the data in cache it will        	always retrieve from cache instead of DB we can prove this as the databse query is not showed in console.
*  The same way we need to do update the cache once that user object got updated and deleted.
	<b>@CachePut & @CacheEvict </b> are used for this.





