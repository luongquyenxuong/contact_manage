package com.example.interactionservice.service.authentication;


import com.example.interactionservice.dto.ResponseDTO;
import com.example.interactionservice.dto.UserDTO;

public interface AuthenticationService {

    ResponseDTO<String> authenticateUser(UserDTO userDTO);

}
