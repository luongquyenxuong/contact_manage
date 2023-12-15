package com.example.manage_contacts.service.impl;

import com.example.manage_contacts.constant.Constant;
import com.example.manage_contacts.constant.UriConstant;
import com.example.manage_contacts.dto.ContactDTO;
import com.example.manage_contacts.dto.InteractionDTO;
import com.example.manage_contacts.dto.InteractionStatisticsDTO;
import com.example.manage_contacts.dto.ResponseDTO;
import com.example.manage_contacts.entity.Contact;
import com.example.manage_contacts.entity.User;
import com.example.manage_contacts.entity.UserContact;
import com.example.manage_contacts.exception.BaseException;
import com.example.manage_contacts.mapper.ContactMapper;
import com.example.manage_contacts.repository.ContactRepository;
import com.example.manage_contacts.repository.UserContactRepository;
import com.example.manage_contacts.security.CustomUserDetails;
import com.example.manage_contacts.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final UserContactRepository userContactRepository;

    private final RestTemplate restTemplate;

    /**
     * Lấy danh sách các liên lạc của người dùng hiện tại được phân trang dựa trên các tiêu chí lọc được cung cấp.
     *
     * @param name   Tên của liên lạc để lọc.
     * @param phone  Số điện thoại của liên lạc để lọc.
     * @param email  Địa chỉ email của liên lạc để lọc.
     * @param page   Số trang cho phân trang. Phải lớn hơn 0.
     * @return Một đối tượng {@code ResponseDTO} chứa danh sách phân trang của các đối tượng {@code ContactDTO}.
     * @throws IllegalArgumentException nếu số trang được cung cấp nhỏ hơn hoặc bằng 0.
     * @see ResponseDTO
     * @see ContactDTO
     */
    @Override
    public ResponseDTO<Page<ContactDTO>> getListContactUser(String name, String phone, String email, Integer page) {

        User user =  getCurrentUser();

        Pageable pageable = PageRequest.of(page - 1, Constant.LIMIT_SIZE_PAGE);

        Page<ContactDTO> contactPage = userContactRepository.findAllByIdUser(user.getId(), name, phone, email, pageable).map(contact -> ContactMapper.convertToDto(contact, user.getId()));


        return ResponseDTO.<Page<ContactDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách thành công")
                .data(contactPage)
                .build();
    }


    /**
     * Tạo mới một liên lạc dựa trên thông tin từ một đối tượng ContactDTO.
     *
     * @param contactDTO Đối tượng ContactDTO chứa thông tin cần tạo mới cho liên lạc.
     * @return Một {@code ResponseDTO} chứa thông tin về trạng thái tạo mới thành công và chi tiết của liên lạc đã được tạo.
     * @throws BaseException Nếu liên lạc đã tồn tại và đang ở trạng thái ACTIVE.
     *                      Nếu liên lạc đã tồn tại nhưng đã bị xóa mềm (SOFT_DELETED), thì sẽ cập nhật lại thành trạng thái ACTIVE.
     *                      Trạng thái lỗi CONFLICT được trả về nếu liên lạc đã được tạo hoặc đã bị xóa mềm trước đó.
     */
    @Override
    public ResponseDTO<ContactDTO> create(ContactDTO contactDTO) {

        User user =  getCurrentUser();


        Optional<Contact> contactByIdAndUserId = contactRepository.findByPhoneNumberAndUserId(contactDTO.getPhone(), user.getId());

        if (contactByIdAndUserId.isPresent()) {
            Contact contact = contactByIdAndUserId.get();

            if (Constant.ACTIVE.equals(contact.getStatus())) {
                throw new BaseException(HttpStatus.CONFLICT.value(), "Liên hệ đã được tạo!");
            }

            if (Constant.SOFT_DELETED.equals(contact.getStatus())) {
                contact.setStatus(Constant.ACTIVE);
                Contact savedContact = contactRepository.save(contact);

                return ResponseDTO.<ContactDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Thêm liên hệ thành công")
                        .data(ContactMapper.convertToDto(savedContact, user.getId()))
                        .build();
            }
        }

        contactDTO.setId(UUID.randomUUID());
        Contact contact = contactRepository.save(ContactMapper.convertToEntity(contactDTO));


        UserContact userContact = new UserContact();
        userContact.setIdUser(user.getId());
        userContact.setIdContact(contact.getId());
        userContactRepository.save(userContact);


        return ResponseDTO.<ContactDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Thêm liên hệ thành công")
                .data(ContactMapper.convertToDto(contact, user.getId()))
                .build();
    }


    /**
     * Chỉnh sửa thông tin của một liên lạc dựa trên thông tin từ một đối tượng ContactDTO cập nhật.
     *
     * @param updateContactDTO Đối tượng ContactDTO chứa thông tin cần cập nhật cho liên lạc.
     * @return Một {@code ResponseDTO} chứa thông tin về trạng thái chỉnh sửa thành công và chi tiết của liên lạc sau khi chỉnh sửa.
     * @throws BaseException Nếu liên lạc không tồn tại, đã bị xóa trước đó hoặc không thuộc quyền sở hữu của người dùng.
     *                      Trạng thái lỗi NOT_FOUND được trả về nếu liên lạc chưa được tạo.
     *                      Trạng thái lỗi CONFLICT được trả về nếu liên lạc đã bị xóa trước đó.
     */
    @Override
    public ResponseDTO<ContactDTO> edit(ContactDTO updateContactDTO) {

        User user =  getCurrentUser();

        Optional<Contact> contactOptional = contactRepository.findContactByIdAndUserId(updateContactDTO.getId(), user.getId());

        if (contactOptional.isEmpty()) {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), "Liên hệ chưa được tạo!");
        }

        if (contactOptional.get().getStatus().equals(Constant.SOFT_DELETED)) {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), "Liên hệ đã được xóa ");
        }

        updateContactDTO.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Contact contact = contactRepository.save(ContactMapper.convertToEntity(updateContactDTO));


        return ResponseDTO.<ContactDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Sửa liên hệ thành công")
                .data(ContactMapper.convertToDto(contact, user.getId()))
                .build();
    }

    /**
     * Xóa mềm (soft delete) một liên lạc dựa trên ID liên lạc.
     *
     * @param id ID của liên lạc cần xóa.
     * @return Một {@code ResponseDTO} không chứa dữ liệu (Void) để biểu thị việc xóa thành công.
     * @throws BaseException Nếu liên lạc không tồn tại, đã bị xóa trước đó hoặc không thuộc quyền sở hữu của người dùng.
     *                      Trạng thái lỗi NOT_FOUND được trả về nếu liên lạc chưa được tạo.
     *                      Trạng thái lỗi CONFLICT được trả về nếu liên lạc đã bị xóa trước đó.
     */
    @Override
    public ResponseDTO<Void> delete(UUID id) {
        User user =  getCurrentUser();

        Optional<Contact> contactOptional = contactRepository.findContactByIdAndUserId(id, user.getId());

        if (contactOptional.isEmpty()) {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), "Liên hệ chưa được tạo!");
        }
        if (contactOptional.get().getStatus().equals(Constant.SOFT_DELETED)) {
            throw new BaseException(HttpStatus.CONFLICT.value(), "Liên hệ đã được xóa ");
        }
        contactOptional.get().setStatus(Constant.SOFT_DELETED);
        contactOptional.get().setDeletedDate(new Timestamp(System.currentTimeMillis()));

        contactRepository.save(contactOptional.get());

        return ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xóa liên hệ thành công")
                .build();
    }

    /**
     * Lấy chi tiết của một liên lạc dựa trên ID liên lạc.
     *
     * @param idContact ID của liên lạc cần lấy chi tiết.
     * @return Một {@code ResponseDTO} chứa thông tin chi tiết của liên lạc dưới dạng {@code ContactDTO}.
     * @throws BaseException Nếu liên lạc không tồn tại hoặc không thuộc quyền sở hữu của người dùng.
     *                      Trạng thái lỗi NOT_FOUND được trả về nếu liên lạc chưa được tạo.
     */
    @Override
    public ResponseDTO<ContactDTO> fetchContactDetailsById(UUID idContact) {

        User user =  getCurrentUser();

        Optional<Contact> contact = contactRepository.findContactByIdAndUserId(idContact, user.getId());

        if (contact.isEmpty()) {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), "Liên hệ chưa được tạo!");
        }

        return ResponseDTO.<ContactDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm liên hệ thành công")
                .data(ContactMapper.convertToDto(contact.get(), user.getId())).build();
    }

    @Override
    public ResponseDTO<ContactDTO> fetchContactDetailsByPhone(String phone) {

        User user =  getCurrentUser();

        Optional<Contact> contact = contactRepository.findByPhoneNumberAndUserId(phone, user.getId());

        if (contact.isEmpty()) {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), "Liên hệ chưa được tạo!");
        }

        return ResponseDTO.<ContactDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm liên hệ thành công")
                .data(ContactMapper.convertToDto(contact.get(), user.getId())).build();
    }

    /**
     * Lấy lịch sử tương tác của một liên lạc dựa trên ID liên lạc và số trang.
     *
     * @param contactId ID của liên lạc cần lấy lịch sử tương tác.
     * @param page      Số trang của kết quả lịch sử. Phải lớn hơn hoặc bằng 0.
     * @return Một {@code ResponseDTO} chứa danh sách các đối tượng {@code InteractionDTO} đại diện cho lịch sử tương tác.
     * @throws BaseException Nếu có lỗi xảy ra trong quá trình thực hiện yêu cầu.
     *                      Các trạng thái lỗi có thể bao gồm NOT_FOUND, FORBIDDEN, hoặc INTERNAL_SERVER_ERROR.
     */
    @Override
    public ResponseDTO<List<InteractionDTO>> getContactHistory(UUID contactId, int page) {
        try {
            User user = getCurrentUser();
            String apiUrl = String.format(UriConstant.apiUrlInteraction + "/contacts/%s/history?page=%d", contactId, page);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", user.getId().toString());

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<ResponseDTO<List<InteractionDTO>>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
                // Do something with the response body if needed
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Handle 404 Not Found
                throw new BaseException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy");
            } else {
                // Handle other status codes
                throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi không xác định");
            }
        } catch (HttpClientErrorException.Forbidden e) {
            // Handle 403 Forbidden
            throw new BaseException(HttpStatus.FORBIDDEN.value(), "Không có quyền truy cập");
        } catch (Exception e) {
            // Handle other exceptions
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi không xác định");
        }
    }

    /**
     * Lấy thống kê về tương tác từ một dịch vụ ngoại vi dựa trên thông tin của người dùng hiện tại.
     *
     * @return Một {@code ResponseDTO} chứa thông tin về trạng thái lấy thống kê thành công và chi tiết thống kê về tương tác.
     * @throws BaseException Nếu có lỗi xảy ra trong quá trình lấy thống kê, hoặc không thể lấy được dữ liệu từ dịch vụ ngoại vi.
     *                      Trạng thái lỗi INTERNAL_SERVER_ERROR được trả về nếu có lỗi không xác định.
     */
    @Override
    public ResponseDTO<InteractionStatisticsDTO> getStatistics() {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        String apiUrl = String.format(UriConstant.apiUrlInteraction + "/statistics/%s", user.getId());

        ResponseEntity<ResponseDTO<InteractionStatisticsDTO>> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,  // No request entity for a GET request
                ParameterizedTypeReference.forType(ResponseDTO.class)  // Assuming ResponseDTO is a generic class
        );

        // Extract the InteractionStatisticsDTO from the response
        ResponseDTO<InteractionStatisticsDTO> responseDTO = responseEntity.getBody();
        if (responseDTO != null) {
            return responseDTO;
        } else {
            // Handle null response or other error cases
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Không thể lấy được thống kê tương tác");
        }
    }


    private User getCurrentUser() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

}
