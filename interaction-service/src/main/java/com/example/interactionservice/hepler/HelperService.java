package com.example.interactionservice.hepler;

import com.example.interactionservice.constant.UriConstant;
import com.example.interactionservice.dto.ContactDTO;
import com.example.interactionservice.dto.ResponseDTO;
import com.example.interactionservice.dto.UserDTO;
import com.example.interactionservice.util.TokenHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HelperService {


    private final RestTemplate restTemplate;

    /**
     * Gửi yêu cầu để lấy chi tiết của một liên hệ dựa trên {@code idContact} từ API Contact.
     *
     * @param idContact UUID của liên hệ cần lấy thông tin chi tiết.
     * @return Một ResponseEntity chứa thông tin chi tiết của liên hệ nếu thành công.
     * Nếu không thành công, trả về ResponseEntity chứa thông báo lỗi và mã trạng thái tương ứng.
     */
    public ResponseEntity<ResponseDTO<ContactDTO>> fetchContactDetailsById(UUID idContact) {

        String url = UriConstant.apiUrlContact + "/detailsById";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("idContact", idContact);
        HttpHeaders headers = new HttpHeaders();

        // Kiểm tra xem token đã được xác thực hay chưa
        if (TokenHolder.getToken() == null) {
            return new ResponseEntity<>(ResponseDTO.<ContactDTO>builder().status(HttpStatus.NOT_FOUND.value()).message("Chưa được xác thực").build(), HttpStatus.NOT_FOUND);
        }

        // Đặt token vào header để gửi cùng yêu cầu
        headers.set("Authorization", "Bearer " + TokenHolder.getToken());

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Gửi yêu cầu đến API để lấy thông tin chi tiết của liên hệ
        return restTemplate.exchange(
                builder.toUriString(),
                org.springframework.http.HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    /**
     * Gửi yêu cầu đăng nhập sử dụng thông tin từ đối tượng {@code UserDTO} đến API Authentication.
     *
     * @param userDTO Thông tin người dùng cần đăng nhập, chứa tên người dùng và mật khẩu.
     * @return Một ResponseEntity chứa thông tin đăng nhập nếu thành công.
     * Nếu không thành công, trả về ResponseEntity chứa thông báo lỗi và mã trạng thái tương ứng.
     */
    public ResponseEntity<ResponseDTO<UserDTO>> login(UserDTO userDTO) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo request entity với userDTO làm nội dung yêu cầu và headers
        HttpEntity<UserDTO> requestEntity = new HttpEntity<>(userDTO, headers);


        String apiUrl = UriConstant.apiUrlAuthentication + "/login";


        // Gửi yêu cầu đăng nhập đến API Authentication
        return restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    /**
     * Gửi yêu cầu để lấy thông tin chi tiết về người dùng từ API Authentication.
     *
     * @return Một ResponseEntity chứa thông tin chi tiết về người dùng nếu thành công.
     * Nếu không thành công, trả về ResponseEntity chứa thông báo lỗi và mã trạng thái tương ứng.
     */
    public ResponseEntity<ResponseDTO<UserDTO>> getUserDetails() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Kiểm tra xem token đã được xác thực hay chưa
        if (TokenHolder.getToken() == null) {
            return new ResponseEntity<>(ResponseDTO.<UserDTO>builder().status(404).message("Chưa được xác thực").build(), HttpStatus.NOT_FOUND);
        }

        // Đặt token vào header để gửi cùng yêu cầu
        headers.set("Authorization", "Bearer " + TokenHolder.getToken());

        HttpEntity<UserDTO> requestEntity = new HttpEntity<>(headers);


        String apiUrl = UriConstant.apiUrlAuthentication + "/userDetails";

        // Gửi yêu cầu để lấy thông tin chi tiết về người dùng
        return restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
