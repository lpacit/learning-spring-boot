package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentRepository
                .findStudentByEmail(student.getEmail());

        if (studentOptional.isPresent()) {
            throw new IllegalStateException("Email taken");
        }
        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        boolean idExists = studentRepository.existsById(studentId);
        if (!idExists) {
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
        studentRepository.deleteById(studentId);
    }

    @Transactional
    public void updateStudent(Long studentId, String newName, String newEmail) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Student with id " + studentId + " is not present. Can't modify."
        ));

        if (newName != null &&
            newName.length() > 0 &&
            !Objects.equals(student.getName(), newName)) {

            student.setName(newName);
        }

        if (newEmail != null &&
            newEmail.length() > 0 &&
            !Objects.equals(student.getEmail(), newEmail)) {

            Optional<Student> optionalStudent = studentRepository.findStudentByEmail(newEmail);

            if (optionalStudent.isPresent()) {
                throw new IllegalStateException("Email taken");
            }

            student.setEmail(newEmail);
        }
    }
}
