package com.goals.course.manager.dao.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

import static java.lang.Boolean.FALSE;


@Data
@Accessors(chain = true)
@Entity
@Table(name = "course_student", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
public class CourseStudent {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "course_is_completed")
    private Boolean courseIsCompleted;

    public boolean courseIsNotCompleted() {
        return FALSE.equals(courseIsCompleted);
    }

}
