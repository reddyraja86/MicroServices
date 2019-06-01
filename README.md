# MicroServices

Microservices is an echo system where we need to follow certain priciples.

1) Configuration Management
  Here the configuration is saved in Gir repository and Configuration server will read this data from the gut resposritory.
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

