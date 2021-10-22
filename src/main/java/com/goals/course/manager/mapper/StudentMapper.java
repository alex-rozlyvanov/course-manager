package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.dto.UserDTO;

public interface StudentMapper {
    UserDTO toUserDTO(final Student student);
}
