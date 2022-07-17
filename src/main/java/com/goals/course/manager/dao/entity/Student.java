package com.goals.course.manager.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = {"courses"})
@Accessors(chain = true)
@Entity
@Table(name = "students")
public class Student {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    private Set<CourseStudent> courses = new HashSet<>();
}
