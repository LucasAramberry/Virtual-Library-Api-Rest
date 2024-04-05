<h1 align="center" id="title">Virtual-Library-Api-Rest</h1>

<h3>Personal Project Virtual Library Api Rest</h3>

This project, called Virtual Library, is a REST API developed using Java and Spring Boot. It incorporates some technologies like JWT for authentication, Swagger for API documentation, Spring Boot Test for unit and integration tests, environments and profiles, and more detailed technologies used.

<h3>Description:</h3>

The Virtual Library is a virtual platform that allows users to access a variety of functionalities related to the management of a library. Unregistered users can enjoy a limited set of functionalities, while those who register and log in will be able to access a wider range of services, depending on their user role (either user or administrator role).

<h3>Features:</h3>

  - Unregistered Users:
    - Limited access to certain features.
    - Explore book catalog.
    - Search books by title, author, publisher.
    - Account registration.
   
  - Registered users:
    - Role User:
      - Unregistered user functionalities.
      - Make loans.
      - Modify profile.
      - Access each book.

  - Role Admin:
      - Contains all privileges.
      - Access all USER features.
      - User administration (registration, enabled, disabled, change roles, delete)
      - Loan administration (create, enabled, disabled, modify, delete)
      - Book administration (create, enabled, disabled, modify, delete)
      - Author administration (create, enabled, disabled, modify, delete)
      - Publisher administration (create, enabled, disabled, modify, delete)

- Technologies used:
  
  - Java 17
  - Spring Boot 3.2.4
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Security
  - Spring Boot Starter Validation
  - Spring Boot Starter Web
  - JWT (JSON Web Tokens)
  - Swagger
  - Jackson Datatype JSR310
  - Spring Boot DevTools
  - MySQL
  - Lombok
  - Spring Boot Starter Test
  - Spring Security Test

<hr>

*Created by Lucas Aramberry*
<br>
https://www.lucasaramberry.website/
