create table course_instructor
(
    id            uuid not null,
    course_id     uuid,
    instructor_id uuid,
    primary key (id)
);
create table course_student
(
    id                  uuid not null,
    course_is_completed boolean,
    course_id           uuid,
    student_id          uuid,
    primary key (id)
);
create table courses
(
    id    uuid not null,
    title varchar(255),
    primary key (id)
);
create table instructors
(
    id uuid not null,
    primary key (id)
);
create table lessons
(
    id        uuid not null,
    title     varchar(255),
    course_id uuid,
    primary key (id)
);
create table students
(
    id uuid not null,
    primary key (id)
);
alter table course_instructor
    add constraint UKl1581h3076iaekbpawn5tjq04 unique (instructor_id, course_id);
alter table course_student
    add constraint UKkasdome202o1jc8xmwhbrjlmt unique (student_id, course_id);
alter table course_instructor
    add constraint FKrorc0834v8irjhd5kongtxhxk foreign key (course_id) references courses;
alter table course_instructor
    add constraint FK5c57mnmwrl62vxsqsvmn96hwr foreign key (instructor_id) references instructors;
alter table course_student
    add constraint FKlmsbddqkv96q4nijkrxuof3ug foreign key (course_id) references courses;
alter table course_student
    add constraint FKacc7gn1l63go6x8dsx0wdnr38 foreign key (student_id) references students;
alter table lessons
    add constraint FK17ucc7gjfjddsyi0gvstkqeat foreign key (course_id) references courses;
