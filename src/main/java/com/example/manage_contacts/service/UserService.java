package com.example.manage_contacts.service;

import com.example.manage_contacts.dto.ResponseDTO;
import com.example.manage_contacts.dto.UserDTO;

public interface UserService {
    ResponseDTO<UserDTO> registerUser(UserDTO userDTO);
    ResponseDTO<UserDTO> login(UserDTO userDTO);


}
