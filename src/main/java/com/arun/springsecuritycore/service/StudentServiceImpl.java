package com.arun.springsecuritycore.service;

import com.arun.springsecuritycore.domain.Student;
import com.arun.springsecuritycore.mapper.StudentMapper;
import com.arun.springsecuritycore.model.StudentDomain;
import com.arun.springsecuritycore.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author arun on 8/29/20
 */

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;


    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    public List<Student> getStudent(String name) {
        List<StudentDomain> studentDomain = studentRepository.findByName(name);

        List<Student> students = new ArrayList<>();
        studentDomain.forEach(sd -> {
            Student student = studentMapper.studentDomainToStudent(sd);
            students.add(student);
        });

        return students;
    }

    @Override
    public Student getStudentById(Long id) {

        Optional<StudentDomain> student = studentRepository.findById(id);
        return student.map(studentMapper::studentDomainToStudent).orElse(null);
    }
}
