package com.goals.course.manager.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = {"courses"})
@ToString(exclude = {"courses"})
@Accessors(chain = true)
@Entity
@Table(name = "instructors")
public class Instructor {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "instructor")
    private Set<CourseInstructor> courses = new HashSet<>();
}
