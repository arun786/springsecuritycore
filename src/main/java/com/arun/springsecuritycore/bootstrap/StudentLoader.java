package com.arun.springsecuritycore.bootstrap;

import com.arun.springsecuritycore.domain.Student;
import com.arun.springsecuritycore.model.StudentDomain;
import com.arun.springsecuritycore.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Repository;

/**
 * @author arun on 8/29/20
 */

@Repository
public class StudentLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentLoader(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            StudentDomain studentDomain = new StudentDomain()
                    .setAge(10)
                    .setName("Arun" + (i + 1))
                    .setStandard("1");
            studentRepository.save(studentDomain);
        }
    }
}
