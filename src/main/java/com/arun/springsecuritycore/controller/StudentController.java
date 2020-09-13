package com.arun.springsecuritycore.controller;

import com.arun.springsecuritycore.domain.Student;
import com.arun.springsecuritycore.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author arun on 8/29/20
 */

@RestController
@Slf4j
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping(value = "/v1/student", produces = "application/json")
    public ResponseEntity<List<Student>> getStudent(@RequestParam String name) {
        log.info("inside the controller");
        List<Student> students = studentService.getStudent(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping(value = "/v2/student/{id}", produces = "application/json")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        log.info("inside the controller");
        Student studentById = studentService.getStudentById(id);
        return ResponseEntity.ok(studentById);
    }

    @DeleteMapping(value = "/v1/student")
    public ResponseEntity<HttpStatus> deleteStudent(@RequestParam String name) {
        studentService.deleteStudent(name);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/v1/students")
    public ResponseEntity<List<Student>> getStudents() {
        List<Student> students = studentService.getStudents();
        return ResponseEntity.ok(students);
    }

    @DeleteMapping(value = "/v3/student")
    public ResponseEntity<HttpStatus> deleteStudentV3(@RequestParam String name) {
        studentService.deleteStudent(name);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/v3/students")
    public ResponseEntity<List<Student>> getStudentsV3() {
        List<Student> students = studentService.getStudents();
        return ResponseEntity.ok(students);
    }
}
