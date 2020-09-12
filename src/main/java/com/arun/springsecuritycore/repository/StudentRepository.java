package com.arun.springsecuritycore.repository;

import com.arun.springsecuritycore.model.StudentDomain;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author arun on 8/29/20
 */
public interface StudentRepository extends PagingAndSortingRepository<StudentDomain, Long> {

    List<StudentDomain> findByName(String name);

    void deleteByName(String name);
}
