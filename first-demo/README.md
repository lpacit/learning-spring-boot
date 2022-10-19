# First Project
This project has been created directly from Spring Initializer
and it has 3 dependencies:
1. Spring Web
2. Spring Data JPA
3. PostgreSQL Driver

### Things to remember
1. The __test__ folder is where you store all of your testing code.
2. In the __resources__ folder
   1. __application.properties__ is where we configure the properties for the application, as well as __environment variables__
   2. __static__ and __templates__ are for web development (HTML, CSS, JavaScript)
3. When you run this application, you can go to localhost:8080 to visualize the content.  
Add these:
   1. Put __@RestController__ on top of the main class
   2. create a new method with the annotation __@GetMapping__ and make it return a list
   3. you will visualize a JSON on the server page
