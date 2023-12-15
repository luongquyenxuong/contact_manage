package com.example.interactionservice.controller;


import com.example.interactionservice.dto.ResponseDTO;
import com.example.interactionservice.dto.UserDTO;
import com.example.interactionservice.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;


    @PostMapping(value = "/authenticateUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> execute(@RequestBody UserDTO userDTO) {
        ResponseDTO<String> responseDTO=authenticationService.authenticateUser(userDTO);
        return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
    }
}
