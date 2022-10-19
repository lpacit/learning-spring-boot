# First Project
This project has been created directly from Spring Initializer
and it has 3 dependencies:
- Spring Web
- Spring Data JPA
- PostgreSQL Driver

### Things to remember
+ The __test__ folder is where you store all of your testing code. 
+ In the __resources__ folder
  - __application.properties__ is where we configure the properties for the application, as well as __environment variables__
  - __static__ and __templates__ are for web development (HTML, CSS, JavaScript)
+ When you run this application, you can go to localhost:8080 to visualize the content.  
Add these:
  - Put __@RestController__ on top of the main class
  - create a new method with the annotation __@GetMapping__ and make it return a list
  - you will visualize a JSON on the server page  

![alt text](./images/helloworldapi.png "Hello World Json")
