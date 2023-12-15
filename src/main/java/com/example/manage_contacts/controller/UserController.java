package com.example.manage_contacts.controller;

import com.example.manage_contacts.dto.ResponseDTO;
import com.example.manage_contacts.dto.UserDTO;
import com.example.manage_contacts.entity.User;
import com.example.manage_contacts.mapper.UserMapper;
import com.example.manage_contacts.security.CustomUserDetails;
import com.example.manage_contacts.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<UserDTO>> registerUser(@RequestBody UserDTO userDTO) {
        ResponseDTO<UserDTO> responseDTO = userService.registerUser(userDTO);
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<UserDTO>> login(@RequestBody UserDTO userDTO) {
        ResponseDTO<UserDTO> responseDTO = userService.login(userDTO);
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);

    }

    @GetMapping(value = "/userDetails", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<UserDTO>> getDetail() {
        try {
            User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
            UserDTO userDTO = UserMapper.convertToDto(user, null);
            ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy chi tiết user thành công")
                    .data(userDTO)
                    .build();

            return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}
