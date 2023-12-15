package com.example.manage_contacts.service.impl;

import com.example.manage_contacts.constant.Role;
import com.example.manage_contacts.dto.ResponseDTO;
import com.example.manage_contacts.dto.UserDTO;
import com.example.manage_contacts.entity.User;
import com.example.manage_contacts.exception.BaseException;
import com.example.manage_contacts.mapper.UserMapper;
import com.example.manage_contacts.repository.UserRepository;
import com.example.manage_contacts.security.CustomUserDetails;
import com.example.manage_contacts.security.JwtTokenUtil;
import com.example.manage_contacts.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Đăng ký một người dùng mới dựa trên thông tin từ UserDTO được cung cấp.
     * <p>
     * Phương thức này kiểm tra xem tên người dùng đã tồn tại trong cơ sở dữ liệu chưa. Nếu tên người dùng đã
     * được sử dụng, một BaseException với trạng thái NO_CONTENT và một thông báo phù hợp sẽ được ném ra.
     * <p>
     * Mật khẩu của người dùng được băm bằng BCrypt trước khi được lưu trữ trong cơ sở dữ liệu. Người dùng được
     * gán một UUID ngẫu nhiên làm ID và được gán vai trò mặc định là Role.USER.
     *
     * @param userDTO UserDTO chứa thông tin người dùng để đăng ký.
     * @return Một ResponseDTO chứa thông tin về trạng thái đăng ký và người dùng đã đăng ký.
     * @throws BaseException Nếu tên người dùng đã được sử dụng.
     */
    @Override
    public ResponseDTO<UserDTO> registerUser(UserDTO userDTO) {
        Optional<User> userOtp = userRepository.findByUsername(userDTO.getUsername());
        //Kiểm tra xem tên người dùng tồn tại chưa
        if (userOtp.isPresent()) {
            throw new BaseException(HttpStatus.CONFLICT.value(), "Tên người dùng đã tồn tại.");
        }

        // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        // Tạo một đối tượng User từ DTO
        User user = UserMapper.convertToEntity(userDTO);
        user.setId(UUID.randomUUID());
        user.setPassword(encodedPassword);
        user.setRoleId(Role.USER.getRoleId());

        // Lưu người dùng mới vào cơ sở dữ liệu
        userRepository.save(user);

        // Tạo và trả về đối tượng UserRegistrationResponseDTO
        UserDTO userResponse = UserMapper.convertToDto(user, null);


        return ResponseDTO.<UserDTO>builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(userResponse)
                .build();
    }

    /**
     * Xác thực người dùng dựa trên thông tin từ UserDTO chứa tên người dùng và mật khẩu.
     * <p>
     * Phương thức này sử dụng quản lý xác thực của Spring Security để xác nhận tên người dùng
     * và mật khẩu được cung cấp. Nếu quá trình xác thực thành công, chi tiết người dùng được thiết lập
     * trong Context Bảo mật, và một JSON Web Token (JWT) được tạo ra. Sau đó, JWT được bao gồm trong
     * phản hồi cùng với thông tin người dùng trong một UserDTO.
     * <p>
     * Nếu quá trình xác thực thất bại do thông tin đăng nhập không hợp lệ (tên người dùng hoặc mật khẩu không đúng),
     * một ResponseDTO với trạng thái UNAUTHORIZED và một thông báo phù hợp sẽ được trả về.
     *
     * @param userDTO UserDTO chứa tên người dùng và mật khẩu để xác thực.
     * @return Một ResponseDTO chứa trạng thái xác thực, một thông báo và chi tiết người dùng với JWT.
     * @throws BaseException Nếu vai trò người dùng không được tìm thấy trong quá trình xác thực.
     */
    @Override
    public ResponseDTO<UserDTO> login(UserDTO userDTO) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));

            //set thong tin authentication vào Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


            Optional<? extends GrantedAuthority> role = userDetails.getAuthorities().stream().findFirst();

            if (role.isEmpty()) {
                throw new BaseException(HttpStatus.NOT_FOUND.value(), "Not found role user");
            }

            // Generate a JWT token
            Optional<String> token = Optional.ofNullable(jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal()));


            return ResponseDTO.<UserDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Login successful")
                    .data(UserMapper.convertToDto(userDetails.getUser(), token.orElse(null)))
                    .build();

        } catch (BadCredentialsException e) {
            throw new BaseException(HttpStatus.UNAUTHORIZED.value(), "Tài khoản hoặc mật khẩu không chính xác!");
        }
    }
}
