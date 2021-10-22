package com.goals.course.manager.mapper.implementation;

import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.mapper.StudentMapper;
import com.goals.course.manager.service.UserProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentMapperImpl implements StudentMapper {
    private final UserProvider userProvider;

    @Override
    public UserDTO toUserDTO(final Student student) {
        return userProvider.findUserById(student.getId()).orElse(null);
    }
}
