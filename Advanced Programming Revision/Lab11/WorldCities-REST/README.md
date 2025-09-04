Lab 11
[valid 2024-2025]
REST Services
Continue the application created at lab 8 or lab 9 integrating the following functionalities:

Implement REST services needed to communicate with the server side data (CRUD).
The main specifications of the application are:

Compulsory (1p)

Create a Spring Boot project that will contain the REST services for communicating with the server data.
Create a REST controller containing a method for:
obtaining the list of countries, via a HTTP GET request.
Test your service using the browser and/or Postman, or RestMan, etc.
Homework (2p)

Create REST services for:
obtaining the list of cities, via a HTTP GET request.
adding a new city, via a HTTP POST request.
modifying the name of a city, via a HTTP PUT request.
deleting a city, via a HTTP DELETE request.
Create a simple client application that invokes the services above, using the support offered by Spring Boot.
Document your services using Swagger or a similar tool.
Bonus (2p)

Create a service that assigns a color to each country, such that two neighboring countries do not have the same color, using a minimum number of colors.
Secure this service using JSON Web Tokens.

Resources

What is REST?
Spring Boot Reference Documentation
Objectives

Understand the concept of application framework.
Get familiar with Spring Boot.
Understand REST software architectural style.
Write programs that communicate with other programs on the network, using HTTP.