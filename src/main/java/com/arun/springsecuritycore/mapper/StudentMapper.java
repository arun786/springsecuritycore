package com.arun.springsecuritycore.mapper;

import com.arun.springsecuritycore.domain.Student;
import com.arun.springsecuritycore.model.StudentDomain;
import org.mapstruct.Mapper;

/**
 * @author arun on 8/29/20
 */

@Mapper
public interface StudentMapper {
    Student studentDomainToStudent(StudentDomain studentDomain);
}
