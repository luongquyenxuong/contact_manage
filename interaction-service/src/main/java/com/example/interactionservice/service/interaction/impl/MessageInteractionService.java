package com.example.interactionservice.service.interaction.impl;

import com.example.interactionservice.constant.InteractionType;
import com.example.interactionservice.dto.ContactDTO;
import com.example.interactionservice.dto.InteractionDTO;
import com.example.interactionservice.dto.ResponseDTO;
import com.example.interactionservice.entity.Interaction;
import com.example.interactionservice.hepler.HelperService;
import com.example.interactionservice.mapper.InteractionMapper;
import com.example.interactionservice.repository.InteractionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service("messageInteractionService")
public class MessageInteractionService extends InteractionServiceImpl {


    public MessageInteractionService(InteractionRepository interactionRepository, HelperService helperService) {
        super(interactionRepository, helperService);
    }

    /**
     * Lưu một tương tác mới cho một liên hệ dựa trên ID liên hệ.
     *
     * @param idContact ID của liên hệ cần lưu tương tác.
     * @return Một {@code ResponseDTO} chứa thông tin về trạng thái lưu tương tác và chi tiết về tương tác nếu thành công.
     * Nếu không thành công, trả về thông báo lỗi từ dịch vụ hỗ trợ hoặc thông tin lỗi từ helperService.
     * Trạng thái lỗi có thể là HTTP status code hoặc các trạng thái lỗi đặc biệt khác.
     */
    @Override
    public ResponseDTO<?> saveInteraction(UUID idContact) {
        // Gửi yêu cầu để lấy chi tiết liên hệ từ helperService
        ResponseEntity<ResponseDTO<ContactDTO>> responseEntity = helperService.fetchContactDetailsById(idContact);

        // Kiểm tra xem yêu cầu lấy chi tiết liên hệ có thành công hay không
        if (responseEntity.getStatusCode().is2xxSuccessful() &&
                responseEntity.getBody() != null &&
                responseEntity.getBody().getStatus() != null &&
                responseEntity.getBody().getStatus() <= HttpStatus.OK.value()) {

            // Tạo một đối tượng Interaction mới
            Interaction interaction = new Interaction();
            interaction.setId(UUID.randomUUID());
            interaction.setInteractionDate(new Timestamp(System.currentTimeMillis()));
            interaction.setInteractionType(InteractionType.MESSAGE);
            interaction.setContactId(responseEntity.getBody().getData().getId());
            interaction.setUserId(responseEntity.getBody().getData().getIdUser());

            // Lưu đối tượng Interaction vào cơ sở dữ liệu
            interactionRepository.save(interaction);

            // Trả về ResponseDTO thông báo lưu tương tác thành công và chi tiết về tương tác đã lưu
            return ResponseDTO.<InteractionDTO>builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Thêm tương tác thành công")
                    .data(InteractionMapper.convertToDto(interaction))
                    .build();
        }

        // Trả về thông báo lỗi từ helperService
        return responseEntity.getBody();
    }
}
