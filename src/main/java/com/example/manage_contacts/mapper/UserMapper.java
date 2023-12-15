package com.example.manage_contacts.mapper;

import com.example.manage_contacts.constant.Role;
import com.example.manage_contacts.dto.UserDTO;
import com.example.manage_contacts.entity.User;


public class UserMapper {
    private UserMapper() {
    }

    public static User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setRoleId(Role.USER.getRoleId());
        return user;
    }

    public static UserDTO convertToDto(User user,String token) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        if(token!=null){
            userDTO.setToken(token);
        }
        userDTO.setRole(Role.getById(user.getRoleId()));
        return userDTO;
    }

}
