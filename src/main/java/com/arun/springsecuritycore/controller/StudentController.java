package com.arun.springsecuritycore.controller;

import com.arun.springsecuritycore.domain.Student;
import com.arun.springsecuritycore.service.StudentService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author arun on 8/29/20
 */

@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/v1/student")
    public ResponseEntity<List<Student>> getStudent(@RequestParam String name) {
        List<Student> students = studentService.getStudent(name);
        return ResponseEntity.ok(students);
    }
}
