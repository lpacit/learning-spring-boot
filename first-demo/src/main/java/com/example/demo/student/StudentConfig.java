package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static java.time.Month.*;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository) {
        return args -> {
            Student mariam = new Student(
                    1L,
                    "Mariam",
                    "Mariam.jamal@gmail.com",
                    LocalDate.of(1997, AUGUST,10)
            );

            Student alex = new Student(
                    "Alex Del Piero",
                    "alex.delpiero@gmail.com",
                    LocalDate.of(1974, NOVEMBER,9)
            );

            repository.saveAll(
                    List.of(mariam, alex)
            );
        };
    }
}
