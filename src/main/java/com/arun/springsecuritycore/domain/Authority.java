package com.arun.springsecuritycore.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * @author arun on 9/11/20
 */

@Getter
@Setter
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String roles;

    @ManyToMany(mappedBy = "authorities")
    private Set<UserDomain> userDomain;
}
