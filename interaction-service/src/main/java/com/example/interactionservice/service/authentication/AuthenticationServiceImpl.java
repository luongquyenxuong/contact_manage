package com.example.interactionservice.service.authentication;


import com.example.interactionservice.dto.ResponseDTO;
import com.example.interactionservice.dto.UserDTO;
import com.example.interactionservice.hepler.HelperService;
import com.example.interactionservice.util.TokenHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final HelperService helperService;

    /**
     * Thực hiện xác thực người dùng dựa trên thông tin đăng nhập từ đối tượng {@code UserDTO}.
     *
     * @param userDTO Thông tin người dùng cần xác thực, chứa tên người dùng và mật khẩu.
     * @return Một {@code ResponseDTO} chứa thông tin về trạng thái xác thực và token nếu thành công.
     * Nếu không thành công, trả về thông báo lỗi và mã trạng thái tương ứng.
     * Trạng thái lỗi có thể là HTTP status code hoặc các trạng thái lỗi đặc biệt khác.
     */
    @Override
    public ResponseDTO<String> authenticateUser(UserDTO userDTO) {
        try {
            // Gửi yêu cầu để xác thực thông tin đăng nhập từ dịch vụ hỗ trợ
            ResponseEntity<ResponseDTO<UserDTO>> responseEntity = helperService.login(userDTO);

            // Xử lý response tùy thuộc vào kết quả xác thực
            if (responseEntity.getStatusCode().is2xxSuccessful() &&
                    responseEntity.getBody() != null &&
                    responseEntity.getBody().getStatus() != null &&
                    responseEntity.getBody().getStatus() <= HttpStatus.ALREADY_REPORTED.value()) {

                // Nếu xác thực thành công, lưu token vào holder và trả về thông tin xác thực thành công
                TokenHolder.setToken(responseEntity.getBody().getData().getToken());

                return ResponseDTO.<String>builder().status(HttpStatus.OK.value()).message("Xác thực thành công").data(TokenHolder.getToken()).build();
            }
        } catch (HttpClientErrorException.Unauthorized unauthorizedException) {

            return ResponseDTO.<String>builder().status(HttpStatus.UNAUTHORIZED.value()).message("Xác thực thất bại").build();
        }

        // Thêm lệnh trả về mặc định ở cuối phương thức
        return ResponseDTO.<String>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Lỗi không xác định").build();
    }


}
