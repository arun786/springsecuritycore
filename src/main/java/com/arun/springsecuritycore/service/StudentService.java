package com.arun.springsecuritycore.service;

import com.arun.springsecuritycore.domain.Student;

import java.util.List;

/**
 * @author arun on 8/29/20
 */
public interface StudentService {

    List<Student> getStudent(String name);

    Student getStudentById(Long id);

    void deleteStudent(String name);
}
