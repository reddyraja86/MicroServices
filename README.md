# MicroServices
Microservices is an echo system where we need to follow certain priciples.  
Design Patterns has to be implemented :  
1)Configuration  
2)Discovery Services  
3)API gateway   
	3.1) Client Side LoadBalancing using Ribbon & API gateway   
	3.2) RestTemplate vs Feign Client  
4)Circuit breaker / fault tolerance using Hystrix   
5)Distributed Caching / Session using Redis Cache  
6)Event Driven Architecture using spring clod stream  
7)Distributed Tracing   
8)Monitoring micro services    
9)Log Aggregator  
10)Spring Visualizagtion using Dashboard  
11)Testing Microservices  

##  Pending Tasks  
- [ ]  Transactions in MicroServices
- [ ]  data sharing between micro services  (CQRS & SAGA design patterns)  
- [ ]  Need to identify the service mesh and side car design patterns.  
- [ ]  Need to complete Oauth2  
- [ ]  Need to implement config server & discovery with consul  
- [ ]  Non-blocking and asynchronous api gateways  
- [ ]  Spring cloud bus   
 
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

### 3.2) RestTemplate vs Feign Client (using the ribbon) :

####  RestTemplate : 
*  Invoking the another service from API gateway to aggregate the response.For exmaple we will call multiple services to return data or return xml response to some client/JSON to other.  
*  This will be useful when we dont want to hardcode the restservice url and port.We can make use service name to invoke the service.  
*  We will use <b>@LoadBalanced</b> annotation for client side load balacing using ribbon    

		package com.springAPIGateway;

		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.cloud.client.loadbalancer.LoadBalanced;
		import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
		import org.springframework.context.annotation.Bean;
		import org.springframework.web.client.RestTemplate;

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

			@Bean
			@LoadBalanced
			public RestTemplate restTemplate() {
				return new RestTemplate();
			}

		}
		
*  We are calling the  another service from api gateway with service name.  

		package com.springAPIGateway;

		import org.springframework.beans.factory.annotation.Autowired;
		import org.springframework.http.HttpEntity;
		import org.springframework.http.HttpHeaders;
		import org.springframework.http.HttpMethod;
		import org.springframework.http.ResponseEntity;
		import org.springframework.web.bind.annotation.GetMapping;
		import org.springframework.web.bind.annotation.RequestMapping;
		import org.springframework.web.bind.annotation.RestController;
		import org.springframework.web.client.RestTemplate;



		@RestController
		@RequestMapping("/access")
		public class RestTemplateVsFeignClient {

			@Autowired
			RestTemplate restTemplate;

			@GetMapping("/restTemplate")
			public String clientSideLoadBalacing() {
				HttpHeaders headers = new HttpHeaders();
				ResponseEntity<String> response = this.restTemplate.exchange("http://eureka-client/client/loadBalance",
						HttpMethod.GET, new HttpEntity<>(headers), String.class);

				return response.getBody();
			}

		}
		
  <b> Note:  Here the eureka-client is the service name which is running on different ports </b>  

   *  Access this url it will load balance the client service  http://localhost:8084/access/restTemplate

####  Feign Client : 

*  This is from neflix api it is a simplified way calling the services.This will connect to discoverr service and take care of loadbalancing.  

*  Need to add the FeignClient Dependency to spring boot application.  
*  Enable the feign client annotation  @EnableFeignClients.  

		package com.springAPIGateway;

		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.cloud.client.loadbalancer.LoadBalanced;
		import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
		import org.springframework.cloud.openfeign.EnableFeignClients;
		import org.springframework.context.annotation.Bean;
		import org.springframework.web.client.RestTemplate;

		@SpringBootApplication
		@EnableZuulProxy
		@EnableFeignClients
		public class SpringApiGatewayZuulDemoApplication {

			public static void main(String[] args) {
				SpringApplication.run(SpringApiGatewayZuulDemoApplication.class, args);
			}

			@Bean
			public PreFilter preFilter() {
				return new PreFilter();
			}

			@Bean
			@LoadBalanced
			public RestTemplate restTemplate() {
				return new RestTemplate();
			}

		}

*  Create @FeignClient interface which will have the name as the client service name .
*  We will define the cleint methods in this interfacce.(Nothing but copy the method details from client service to here). 

		package com.springAPIGateway;

		import org.springframework.cloud.openfeign.FeignClient;
		import org.springframework.web.bind.annotation.GetMapping;

		@FeignClient(decode404=true,name="eureka-client")
		public interface TestFeignClient {

			@GetMapping("/client/loadBalance")
			public String clientSideLoadBalacing();
		}

*  Invoke this method from rest controller.  

		@RestController
		@RequestMapping("/access")
		public class RestTemplateVsFeignClient {

			@Autowired
			RestTemplate restTemplate;

			@Autowired
			TestFeignClient testFeignClient;

			@GetMapping("/restTemplate")
			public String clientSideLoadBalacing() {
				HttpHeaders headers = new HttpHeaders();
				ResponseEntity<String> response = this.restTemplate.exchange("http://eureka-client/client/loadBalance",
						HttpMethod.GET, new HttpEntity<>(headers), String.class);

				return response.getBody();
			}

			@GetMapping("/feignClient")
			public String clientSideLoadBalacingFeign() {
				System.out.println("---test");
				return testFeignClient.clientSideLoadBalacing();
			}
		}

## 4)Circuit breaker / fault tolerance using Hystrix or resilience4j :

*  When ever SERVICE 1 is invoking SERVICE 2.  

	SERVICE 1	------->	SERVICE 2  
	
* Once the user gives a request from thread a pool a thread will be assigned to the user to process the request.  
* once the SERVICE 2 is not up and running then there will be a delay in response.  
* All the subsequent which will go to SERVICE 2 may fail which will consume number of resource like threads in thred pool that are waiting for response. Precious resources such as threads might be consumed in the caller while waiting for the other service to respond.     
* To solve this we have circuit breaker pattern where you will have a circuit at SERVICE 1  and the request will go through this circuit before reaching SERVICE 2.  
#### Circuit breaker pattern :  
* When number of consecutive failures crosees certain threshhold the circuit breaker breaks and give the default response configure.  
* For certain duration all the attempts to invoke this service will fail immedidately.  
* After the timeout expires the circuit breaker allows a limited number of test requests to pass through. If those requests succeed the circuit breaker resumes normal operation. Otherwise, if there is a failure the timeout period begins again.   

#### Different States of the Circuit Breaker :   
The circuit breaker has three distinct states: Closed, Open, and Half-Open:

* <b>Closed –</b> When everything is normal, the circuit breaker remains in the closed state and all calls pass through to the services. When the number of failures exceeds a predetermined threshold the breaker trips, and it goes into the Open state. 
* <b>Open –</b> The circuit breaker returns an error for calls without executing the function. 
* <b>Half-Open –</b> After a timeout period, the circuit switches to a half-open state to test if the underlying problem still exists. If a single call fails in this half-open state, the breaker is once again tripped. If it succeeds, the circuit breaker resets back to the normal, closed state.  

#### Bulkhead pattern :  
* In general, the goal of the bulkhead pattern is to avoid faults in one part of a system to take the entire system down.  
* When user give a request thread pool will assign one thread and service the response to the user with respective micro service.  
* Users can give request to different services and for evey user a thread is assigned from the pool.  
* In above scenario if one of the service fails to serve the response or will be slow to give the response then the assigned thread cannot be released to thread pool or become free to serve other users.  
* In this case we will devide the thread pool with number of services and will set threshhold to the service.ex: At time max 3 threads can serve in case of thread pool with 9 threads and 3 services in system.  












##  5)Distributed Caching using Spring Redis Cache :

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

####  Distributed session Management using redis:  

*  Once the user is logged in we can save his data in cache and other services can use this session data for authentication or read user related session which will be useful to process user request.  
*  Once the user is logged out we can remove that session data from cache so that for every request this session data will be checked.  

##  7) Event Driven Architecture using spring cloud stream  :  

*  Communication between two different micro services can be done in two ways
	*  Synchronous Communication  
	*  Asyncronous Communication  
	
####  Synchronous Communication :     

*  Here once service will invoke another service and will wait for the response.Once the response is recieved service will proceed with its functionality. 
*  We will make use of Feign Client or Spring Rest Template for the communcation.  

####  Asynchronous Communication :  

*  Here we will make use RabbitMQ or any other queue for the communication.  
*  Spring will provide a Clous Stream which will be used for the commucation with any message.  

####  Mesaage Queue Communication with Spring Cloud Stream :  

*  We need to follow the below steps for communicatin with the MQ   
	*  Creating a source which will Communicate with the output channel whcih will be responsible for sending the data to MQ.  

##### Spring Cloud Concepts for publishing a Message -
* Binder - Depending upon the messaging system we will have to specify a the messaging platform dependency, which in this case is RabbitMQ  
<dependency> <groupId>org.springframework.cloud</groupId> <artifactId>spring-cloud-starter-stream-rabbit</artifactId> </dependency>
* Source - When a message is needed to be published it is done using Source. The Source is an interface having a method annotated with @Output. The @Output annotation is used to identify output channels. The Source takes a POJO object, serializes it and then publishes it to the output channel.  


			public interface EmployeeRegistrationSource {

			    @Output("employeeRegistrationChannel")
			    MessageChannel employeeRegistration();

			}

* Channel - A channel represents an input and output pipe between the Spring Cloud Stream Application and the Middleware Platform. A channel abstracts the queue that will either publish or consume the message. A channel is always associated with a queue.  

*  Enable the source by

		@EnableBinding(EmployeeRegistrationSource.class)
		
* Publish the data using the source and output channel

		employeeRegistrationSource.employeeRegistration().send(MessageBuilder.withPayload(employee).build());

* Configure the Rabbit Mq detail in Application.properties for publishing a message 		

		server.port=8080
		spring.rabbitmq.host=localhost
		spring.rabbitmq.port=5672
		spring.rabbitmq.username=guest
		spring.rabbitmq.password=guest

		spring.cloud.stream.bindings.employeeRegistrationChannel.destination=employeeRegistrations
		spring.cloud.stream.default.contentType=application/json

##### Spring Cloud Concepts for consuming a Message -

* Sink - In Spring Cloud Stream, sink is used to consume message from queue. @StreamListener(target = Sink.INPUT) public void processRegisterEmployees(String employee){ System.out.println("Employees Registered --"+ employee); }  

* In application.properties file updated the queue on which we will listen  

		server.port=8090
		spring.rabbitmq.host=localhost
		spring.rabbitmq.port=5672
		spring.rabbitmq.username=guest
		spring.rabbitmq.password=guest

		spring.cloud.stream.bindings.input.destination=employeeRegistrations
		spring.cloud.stream.bindings.input.group=employeeRegistrationQueue

* Enable the SinkListner and Stream Listener with the respective queue.  

		@SpringBootApplication
		@EnableBinding(Sink.class)
		public class SpringStreamConsumerDemoApplication {

			public static void main(String[] args) {
				SpringApplication.run(SpringStreamConsumerDemoApplication.class, args);
			}

			@StreamListener(target = Sink.INPUT)
			public void processRegisterEmployees(String employee) {
				System.out.println("Employees Registered by Client " + employee);
			}
		}



##  8) Distributed tracing using the sleuth  : 

*  In micro services environment tracing the user request when it is flowing through different services is difficult.So we will make use of slueth will generate unique ids for tracking.  

*  Slueth will generate Trace Id once the request enters and for  every hop bretween the services it will generate span ID,by this way we can identify how much time evry span took for request completion.  

*  Spring Cloud Sleuth adds two types of IDs to your logging, one called a trace ID and the other called a span ID. The span ID represents a basic unit of work, for example sending an HTTP request. The trace ID contains a set of span IDs, forming a tree-like structure. The trace ID will remain the same as one microservice calls the next. 

*  Adding sleuth dependency to the services 

			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-sleuth</artifactId>
			</dependency>

			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-sleuth-zipkin</artifactId>
			</dependency>


*  This will be generating unique trace and span ids.  
*  Zipkin server is used to display this information in proper format.  

#### Configure zipkin server :

*  Go to openaipkin and run docker image or identify other options to run zipkin server.  
*  By default zipkin will run on 9411 port and this will disaply the trace and span information.  


##  9)Monitoring micro services : 

* Micrometer is something which will provide interface to integrate actuator metrics to external monitoring system.It supports different monitoring systems like prometheus,Netflix atlas.  
* By integrating mictometer prometheus it will give a new endpoint to actuator which will give additional metrics. 

#### Integrating prometheus  : 
*   Adding the maven dependency 


		<dependency>
			<groupId>io.micrometer</groupId>
			<artifactId>micrometer-registry-prometheus</artifactId>
		</dependency
		
*   Addd the following properties in application.prop  

		management.endpoints.web.exposure.include=*
		management.endpoint.health.show-details=always
		
*  Accessing http://localhost:8084/actuator will give additional end point for prometheus.  
		
		http://localhost:8084/actuator/prometheus  
		
*  Add the prometheus.yml file contains config 

*  Run the docker image and point the above yml file 

		docker container  run -d --name=prometheus -p 9090:9090 -v C:\Users\rrayappa\git\MicroServices\Spring-APIGateway-Zuul-demo\prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus --config.file=/etc/prometheus/prometheus.yml
		
*  we can see the prometheus with different metrics

		http://localhost:9090  

####  Graphana configuration : 

Graphana is a monitoring system which can get the data from various resources like prometheus.
we can configure rules to sent emails when something gows down etc.

*  Graphana will get the details from prometheus and show it in a better format.It will have rules by which we can sent emails.  

*  Graphana can feed the data from different sources .  

*  start graphana from docker  

		docker run -d --name=grafana -p 3000:3000 grafana/grafana  
		
*  Congure the data source and craete required  graph.

## Spring Security :  

Spring will have   
Basic Security :  
 		Spring security module has the  org.springframework.security.core.userdetails.UserDetailsService interface which will allow to integrate  with the other services for user authentication.  

		@Service
		class UsersService  implements org.springframework.security.core.userdetails.UserDetailsService{

			@Autowired
			UserRepository userRepo;

			@Override
			public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

				return userRepo.findByUserName(userName).
						map(u -> new User(u.getUserName(), u.getPassWord(),u.isActive() , u.isActive(), 	u.isActive(), u.isActive(), 
						AuthorityUtils.createAuthorityList("ADMIN","USER"))).
						orElseThrow(()->new UsernameNotFoundException("UserName"+userName+"-- Not Found"));
			}

		}

In above we have integrated with the userRepository and get the userdetails.Once we got the user details from repository we will
return the spring security User object which is an implementatiton of User Details Service.  



# Microservices Design Patterns  

1.CQRS  
2.Event Sourcing  
3.SAGA  
4.Circuit Breaker  
5.BulkHead   


## Domain Driven Design  

 We can solve the problems by connecting the software with the domainba expert language as they have solution for problems realted to  domain.DDD will allow us to use the doamina expert language with the developers implementation.
 Here we will design large application into multiple bounded contexts.
 With Bounded context we can define the entities and the realtions between them.

## Bounded Context :  
 
 This is the boundary of fucntionality and names used in that bounded context.
 Following are two different bounded contexts and the entities are same with different names.
 
 For Ex:
  
  Hr Department									Engg Department
  Resource									Engineer
 
With the help of DDD we can identify
*  Whate are the Entities  
*  Relation between the entities  
*  Events needed for communication between the entities.  
 
## Data Consistency :  

In case of RDBMS we have optimistic locking and pessimistic locking will be used to maintain the data consistency.

We need to have a consistent and highly available system.

For high availability we have can use optimistic locking but the draw back is only one user data will be updated in DB and remaining users data cannot be saved.

### Optimstic Locking :
 In optimisitc locking we will allow multiple users to update and before updating the record in DB we will check whether the user record   version and database version are same or not.
 For this we will maintain unique value like version or timestamp.This value will be compared before updating the record.
 This is useful when we have limited number of users so that their data wont be lost.
 
 for Ex: updating the wiki page only few users will update and when saved who ever first saved that data will be updated in DB.

#### Pessimistic Locking :

 We will maintain locking on a record so that other users cannot update the same record. This will maintain consistency but reduces   availability.
 
 
#### Distributed Transactions :

 In case of distributed transactions how to improve the availability and consistency 

 1) Master slave concept :
   we can have multiple databases aligned one for write and remaining for the read operation.
   This will improve the availability but we will miss the consistency.
 2) Sharding :
   Here we will create multiple db instances and each instance will have specific rule and data which follow this rules will be saved in    specific instance.
   for Ex: user names starting A- L  will be saved in instance-1 and L-z in instance-2.
   Here there might be more using starting with A-L this will improves traffic at instance-1.
	
  #### CAP Theorem : (Consistency availability Partition)
   As per the CAP theorem we can have only two combinations in any distributed database system.

   We need a high available and distributed transaction supported system.


## CQRS: 

 In case of CQRS we will have Read and write databases are separated.

Why :
	we can maintain indexes for the read database this will improve the read performance as the data will be arranged in balanced  binary tree.Not required indexes in write database will will improve the performance of save operation.
There is possibility that consistency will be lost.
	
	

### Maintain the transaction across different DBs(Micro services or distributed systems) can be done in different ways :
1) Two phase commit
2) Event sourcing

* Two phase commit  : 
  In two phase commit we will have coordinator who will coordinate the transactions
  we have prepare phase( prepate the data and gets the confirmations) and commit phase( commit the data and get the confirmations).

 
	
	coordinator						Service-1			Service-2
	
	
					----------------->	
	prepare
					------------------------------------->
	
					<----------------
					<----------------------------------------
	
					---------------->	
	Commit
					------------------------------------->
	
					<----------------
					<----------------------------------------
	

   This is a slow process as there is a coordinator and multiple calls

  code https://www.hhutzler.de/blog/a-deeper-dive-into-jpa-2-phase-commit-2pc-and-rac/

## Event Sourcing :

The concept behind event sourcing is that every change to the state of the application should be captured. In other words, every action performed on an application entity should be persisted. Then, we can query those events. We can also use the list of events to reconstruct the current state of the object.  
Ex: Google pay where we will capture the events like payment pais,recived etc.  

 Instead of saving the object state we will maintain the sequence of events and based on these event we will identify the object  state.
 We will have the list of events in a queue and respective services will fetch and do the operations based on event type.  

Why :
 This will not stop the users from doing their work like creating.Ex :coffee  service creation will never be stopped.  
 Not required transaction manager or auditing system to maintain history ..as the events will take care of this.  
 In case of distributed transactions we have to problems like availability but in case of event sourcing we will continue to send the  event types and respective operations will be performed based on event types.  
 

## CQRS and event source will work together :

In case of CQRS in order to sync the read database with write database we will generate one event and one of the microservice will be triggered by
this event type and all the operations will be performed based on these events.

For example :

We have user creation request

	CreatSUer service 	-----> 	USER_CREATED_EVENT + payload	----->	event spurce will hadle this event type
						
						succsess ---->  User will be created in WRITE & ALL the Read databases
						Failure  ---->   In case of any failure a seperate event will be generated to rollback the data.
						

*  Eventual Consistency :
	In above the read databases will be not consistent for some time after that they will be eventually consistent.

*  Strong Consistency :
	
	In case master slave mechanism we can configure untill all the read databases are updated with the latest changes we will not return read data to the end user.THis will reduce the performance of the application.


##  Communication and transactions between MS:

  Ms will use events which will make sure the transaction is successful or else it will do '
 a retry mechanism or another event to revert the transactions.
 We will use BASE instead of ACID,we use eventual consistency.
  




