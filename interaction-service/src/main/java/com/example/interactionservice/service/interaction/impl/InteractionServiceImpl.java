package com.example.interactionservice.service.interaction.impl;


import com.example.interactionservice.constant.InteractionType;
import com.example.interactionservice.dto.*;
import com.example.interactionservice.entity.Interaction;
import com.example.interactionservice.hepler.HelperService;
import com.example.interactionservice.mapper.InteractionMapper;
import com.example.interactionservice.repository.InteractionRepository;
import com.example.interactionservice.service.interaction.InteractionService;

import com.example.interactionservice.util.UUIDConverter;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("interactionServiceImpl")
@RequiredArgsConstructor
public abstract class InteractionServiceImpl implements InteractionService {

    protected final InteractionRepository interactionRepository;

    protected final HelperService helperService;

    /**
     * Xóa một tương tác dựa trên ID tương tác.
     *
     * @param idInteraction ID của tương tác cần xóa.
     * @return Một {@code ResponseDTO} chứa thông tin về trạng thái xóa tương tác và thông báo nếu thành công hoặc không thành công.
     * Trạng thái lỗi có thể là HTTP status code hoặc các trạng thái lỗi đặc biệt khác.
     */
    @Override
    public ResponseDTO<Void> deleteInteraction(UUID idInteraction) {

        // Gửi yêu cầu để lấy chi tiết người dùng từ helperService
        ResponseEntity<ResponseDTO<UserDTO>> responseEntity = helperService.getUserDetails();

        // Kiểm tra xem yêu cầu lấy chi tiết người dùng có thành công hay không
        if (responseEntity.getStatusCode().is2xxSuccessful() &&
                responseEntity.getBody() != null &&
                responseEntity.getBody().getStatus() != null &&
                responseEntity.getBody().getStatus() <= HttpStatus.OK.value()) {

            // Lấy ID người dùng từ thông tin người dùng
            UUID userId = responseEntity.getBody().getData().getId();

            // Kiểm tra và xóa tương tác nếu tồn tại
            return interactionRepository.checkInteraction(userId, idInteraction)
                    .map(interaction -> {
                        interactionRepository.delete(interaction);
                        return ResponseDTO.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Xóa thành công")
                                .build();
                    })
                    .orElseGet(() -> ResponseDTO.<Void>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Không thể xóa tương tác: Tương tác không tồn tại")
                            .build());
        }

        // Trả về thông báo lỗi nếu không thành công hoặc chi tiết người dùng không hợp lệ
        return ResponseDTO.<Void>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Không thể xóa tương tác: Chi tiết người dùng không hợp lệ")
                .build();
    }

    /**
     * Lấy lịch sử tương tác giữa một người dùng và một liên hệ dựa trên các tham số như ID người dùng, ID liên hệ và trang.
     *
     * @param userId    ID của người dùng cần lấy lịch sử tương tác.
     * @param contactId ID của liên hệ cần lấy lịch sử tương tác.
     * @param page      Trang dữ liệu cần lấy (bắt đầu từ 1).
     * @return Một {@code ResponseDTO} chứa thông tin về trạng thái lấy lịch sử tương tác và danh sách các InteractionDTO nếu thành công.
     * Nếu không có lịch sử tương tác, trả về thông báo không tìm thấy và mã trạng thái NOT_FOUND.
     * Trạng thái lỗi có thể là HTTP status code hoặc các trạng thái lỗi đặc biệt khác.
     */
    @Override
    public ResponseDTO<List<InteractionDTO>> getHistory(UUID userId, UUID contactId, Integer page) {
        // Xây dựng trang dữ liệu dựa trên tham số page
        Pageable pageable = PageRequest.of(page - 1, 10);

        // Lấy trang lịch sử tương tác từ repository
        Page<Interaction> interactionPage = interactionRepository.getHistory(userId, contactId, pageable);
        // Kiểm tra xem có lịch sử tương tác hay không
        if (interactionPage.isEmpty()) {
            // Trả về thông báo không tìm thấy nếu không có lịch sử tương tác
            return ResponseDTO.<List<InteractionDTO>>builder()
                    .status(HttpStatus.NOT_FOUND.value())  // or any appropriate HTTP status code
                    .message("Không tìm thấy thông tin tương tác")
                    .build();
        }

        // Chuyển đổi trang lịch sử tương tác thành trang InteractionDTO
        Page<InteractionDTO> interactionDTOPage = interactionPage.map(InteractionMapper::convertToDto);

        // Trả về ResponseDTO chứa danh sách InteractionDTO và thông báo thành công
        return ResponseDTO.<List<InteractionDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách thành công")
                .data(interactionDTOPage.stream().toList())
                .build();

    }

    /**
     * Tính toán và trả về thống kê về tương tác của một người dùng dựa trên ID người dùng.
     *
     * @param userId ID của người dùng cần tính toán thống kê tương tác.
     * @return Một {@code ResponseDTO} chứa thông tin về trạng thái tính toán thống kê và đối tượng {@code InteractionStatisticsDTO}
     * nếu thành công. Nếu không thành công, trả về thông báo lỗi và mã trạng thái tương ứng.
     * Trạng thái lỗi có thể là HTTP status code hoặc các trạng thái lỗi đặc biệt khác.
     */
    @Override
    public ResponseDTO<InteractionStatisticsDTO> calculateStatistics(UUID userId) {
        try {
            // Tính toán số lượng tương tác tổng cộng và theo từng loại
            long totalInteractions = interactionRepository.countTotalInteractionsByUserId(userId);
            long emailInteractions = interactionRepository.countInteractionsByType(userId, InteractionType.EMAIL);
            long messageInteractions = interactionRepository.countInteractionsByType(userId, InteractionType.MESSAGE);
            long callInteractions = interactionRepository.countInteractionsByType(userId, InteractionType.CALL);

            // Lấy thông tin về liên hệ tương tác nhiều nhất
            Object[] result = interactionRepository.findMostInteractedContact(userId);
            if (result != null && result.length == 1) {
                UUID contactId = UUIDConverter.bytesToUUID((byte[]) ((Object[]) result[0])[0]);
                long interactionCount = ((Number) ((Object[]) result[0])[1]).longValue();

                // Tạo đối tượng InteractionStatisticsDTO
                InteractionStatisticsDTO statisticsDTO = InteractionStatisticsDTO.builder()
                        .totalInteractions(totalInteractions)
                        .interactionTypeCounts(Map.of(
                                InteractionType.EMAIL, emailInteractions,
                                InteractionType.CALL, callInteractions,
                                InteractionType.MESSAGE, messageInteractions
                        ))
                        .mostInteractingUser(ContactDTO.builder().id(contactId).interactionCount(interactionCount).build())
                        .build();

                // Trả về ResponseDTO chứa thông tin thống kê thành công và đối tượng InteractionStatisticsDTO
                return ResponseDTO.<InteractionStatisticsDTO>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy thông kê tương tác thành công")
                        .data(statisticsDTO)
                        .build();

            } else {
                // Trả về thông báo lỗi nếu không lấy được thông tin về liên hệ tương tác nhiều nhất
                return ResponseDTO.<InteractionStatisticsDTO>builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message("Lấy thông kê tương tác thất bại")
                        .build();

            }
        } catch (Exception e) {
            //  Trả về thông báo lỗi
            return ResponseDTO.<InteractionStatisticsDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lấy thông kê tương tác thất bại")
                    .build();
        }
    }

}
