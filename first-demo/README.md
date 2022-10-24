# First Project
This project has been created directly from Spring Initializer,
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
  - Put `@RestController` on top of the main class
  - create a new method with the annotation `@GetMapping` and make it return a list
  - you will visualize a JSON on the server page  

![alt text](./images/helloworldapi.png "Hello World Json")

## Adding Student Class (API LAYER)
+ Created the Student Class
+ Migrated `RestController` from `DemoApplication` to `StudentController`

## Adding business logic for managing students (Service Layer)
+ Created `StudentService` class which now has the actual job to return students info
+ Things to remember:
  + In the `StudentController` class declare an instance of `StudentService`
  + Invoke the `getStudents` method from _controller_ but __implement it__ in _service_
  + Add `@RequestMapping` (the path you specify is to access the api at `localhost:8080/api/v1/student`)

```java
@RestController
@RequestMapping(path = "api/v1/student")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getStudents(String name) {
        return studentService.getStudents();
    }
}
```
## Dependency injection
+ Added `@Autowired` on top of `StudentController` constructor to auto-instantiate the `studentService` class
+ Added `@Service` on top of `StudentService` class

## Data Access Layer
+ Created PostgreSQL Database and configured _psql_
+ Updated `application.properties` with database informations
```
spring.datasource.url=jdbc:postgresql://localhost:5432/...
spring.datasource.username=...
spring.datasource.password=... 
```
+ Added annotations on Student class to make a database table out of it

```java
@Entity
@Table  
public class Student {
  @Id
  @SequenceGenerator(
          name = "student_sequence",
          sequenceName = "student_sequence",
          allocationSize = 1
  )
  @GeneratedValue(
          strategy = GenerationType.SEQUENCE,
          generator = "student_sequence"
  )

  private Long id;
  private String name;
  private String email;
  private Integer age;
  private LocalDate dateOfBirth;
  ...
}
```

+ Added `StudentRepository` **interface** to use database-ish actions (`findAll` ecc)
```java
@Repository    // Responsible for data access
public interface StudentRepository extends JpaRepository<Student, Long> { }
```
+ Changed the `getStudents` _service_ method to use interface's `findAll` method

```java
@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }
}
```

+ Added `StudentConfig` class to save entries in database
```java
@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository) {
        return args -> {
            Student mariam = new Student(
                    1L,
                    "Mariam",
                    "Mariam.jamal@gmail.com",
                    25,
                    LocalDate.of(1997, AUGUST,10)
            );

            Student alex = new Student(
                    "Alex",
                    "alex.delpiero@gmail.com",
                    30,
                    LocalDate.of(2000, JANUARY,10)
            );

            repository.saveAll(
                    List.of(mariam, alex)
            );
        };
    }
}
```

## Transient section
+ Added the `@Transient` annotation to the `age` attribute
  + the field marked with `@Transient` is ignored by mapping framework and the field not mapped to any database column
+ Changed the logic to calculate the age with `LocalDate`
```java
public Integer getAge() {
    return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
}
```
hence deleted the declaration of the `age` attribute from constructors.

# POST MAPPING
![alt text](./images/post-request.png "POST Request")
+ In `@StudentController`
```java
@PostMapping
public void registerNewStudent(@RequestBody Student student) {
    studentService.addNewStudent(student);
}
```
`RequestBody` takes the request and maps it into the `Student` object

+ Tested APIs with _Postman_
![alt text](./images/postman-test.png "Postman Test")
+ Added logic to check if email added is already present (inside of `StudentService` class)
```java
public void addNewStudent(Student student) {
    Optional<Student> studentOptional = studentRepository
        .findStudentByEmail(student.getEmail());

    if (studentOptional.isPresent()) {
        throw new IllegalStateException("Email taken");
    }
    studentRepository.save(student);
}
```


