package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static java.time.Month.AUGUST;
import static java.time.Month.JANUARY;

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
