package com.goals.course.manager.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = {"students", "instructors", "lessons"})
@ToString(exclude = {"students", "instructors", "lessons"})
@Accessors(chain = true)
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "course")
    private Set<CourseInstructor> instructors = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<CourseStudent> students = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<Lesson> lessons = new HashSet<>();
}
