package com.example.demo.student;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {
    public List<Student> getStudents() {
        return List.of(
                new Student(
                        1L,
                        "Mariam",
                        "Mariam.jamal@gmail.com",
                        21,
                        LocalDate.of(1997,8,10)
                )
        );
    }
}
