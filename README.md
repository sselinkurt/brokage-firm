# brokage-firm
Brokage Firm Application

- The application is written using Java 21 and Spring Boot, following clean architecture principles.
  

1-) mvn clean install

2-) Go to http://localhost:8080 and http://localhost:8080/h2-console/ for h2 database.

3-) Get jwt token from **/auth/login** endpoint
   1. **Admin User**

      The admin username and password are as follows:
      
      ```json
      {
        "username": "admin",
        "password": "pass"
      }
      ```

   2. **Customer**

      Use **/auth/register** endpoint for create a customer. 

      ```json
      {
        "username": "selinkurt",
        "password": "123"
      }
      ```
      After successfully registering, obtain a token using your username and password.

4-) To use other endpoints, add the token as a Bearer token to the header.

   **NOTE**:
   
   *Only the admin user can use the match endpoint. To access this endpoint, obtain an admin token.*
   
   *You can use other endpoints with the customer token you registered with. Customers can only view/create their own data.*
    



