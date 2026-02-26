# Versions

**Outstanding Tasks**
- Exception handling needs to be added in the Truelayer service
- All new code needs to be tested more
- Any issues with us not being able to reach the truelayer api - restart colima first
- All docs need updating.
- Send credit limit with cards update and add to account
- Need to move Truelayer info to other gitlab
- Keycloak logging users out too quickly
- Isa Service needs work

## v1.7.0
**Date: 01/03/2024**

+ Truelayer implemented to fetch accounts and balances 
+ Basic Networth value returned based on all accounts

## v1.6.0
**Date: 28/12/2023**

+ The basic balance functions added to Account Service. 
+ Isa Service created with Kafka introduced to manage inter service notifications.
+ Test suit not completed due to time restraints.

## v1.5.0
**Date: 18/12/2023**

The Account Service was created with basic CRUD operations - more tests need to be added but decision was made to focus on new challenges as time for project running low.


## v1.4.0
**Date: 11/12/2023**

The User Service was completed
+ Login and logout of Keycloak through the Auth Controller
+ CRUD User endpoints
+ Keycloak ID passed to services through headers

## v1.3.0
**Date: 16/11/2023**

Gateway Server, Keycloak Server, and Monitoring tools added
+ The microservice endpoints are now externally available through the Gateway Server at port 8080.
+ Keycloak Server has been added to provide security to the application.
+ Grafana, Loki, Prometheus and Promtail have been added to provide better monitoring and observability functionality.


## v1.2.0
**Date: 14/11/2023**

Discovery Server Added
+ When loaded from Docker Compose, the microservices use Eureka as the discovery server.
+ When loaded in Kubernetes, service discovery is managed by cluster networking and service DNS.

## v1.1.0
**Date: 13/11/2023**

Config Server has been added.
+ Usable via Docker and Helm
+ User Service configurations have been tidied

## v1.0.0
**Date: 09/11/2023**

A basic version of the User Service has been created with minimal CRUD endpoints.
+ MySQL and Liquibase setup and confirmed working.
+ Docker Compose setup and confirmed working.
+ Helm charts setup and confirmed working.
+ Testcontainers and Integration tests setup and confirmed working (although issue with data pollution when running consecutive tests persists.)
