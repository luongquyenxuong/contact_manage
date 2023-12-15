package com.example.interactionservice.controller;

import com.example.interactionservice.dto.InteractionDTO;
import com.example.interactionservice.dto.InteractionRequest;
import com.example.interactionservice.dto.InteractionStatisticsDTO;
import com.example.interactionservice.dto.ResponseDTO;
import com.example.interactionservice.factory.InteractionServiceFactory;
import com.example.interactionservice.service.interaction.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interaction")
public class InteractionController {

    private final InteractionServiceFactory interactionServiceFactory;


    @PostMapping(value = "/add")
    public ResponseEntity<ResponseDTO<?>> addInteraction(@RequestBody InteractionRequest interactionRequest) {

        InteractionService interactionService = interactionServiceFactory.createMyService(interactionRequest.getType());
        ResponseDTO<?> responseDTO = interactionService.saveInteraction(interactionRequest.getIdContact());
        return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<ResponseDTO<?>> deleteInteraction(@RequestBody Map<String, String> requestBody) {
        try {
            String idInteractionString = requestBody.get("idInteraction");
            UUID idInteraction = UUID.fromString(idInteractionString);
            InteractionService interactionService = interactionServiceFactory.createMyService("");
            ResponseDTO<?> responseDTO = interactionService.deleteInteraction(idInteraction);
            return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
        } catch (IllegalArgumentException e) {

            ResponseDTO<?> responseDTO = ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Id không hợp lệ")
                    .build();

            return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
        }

    }

    @GetMapping(value = "/contacts/{contactId}/history")
    public ResponseEntity<ResponseDTO<List<InteractionDTO>>> getContactHistory(@PathVariable UUID contactId, @RequestParam(defaultValue = "1") int page,
                                                                               @RequestHeader("X-User-Id") UUID userId) {

        InteractionService interactionService = interactionServiceFactory.createMyService("");
        ResponseDTO<List<InteractionDTO>> responseDTO = interactionService.getHistory(userId, contactId, page);
        return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
    }

    @GetMapping(value = "/statistics/{useId}")
    public ResponseEntity<ResponseDTO<InteractionStatisticsDTO>> getInteractionStatistics(@PathVariable UUID useId) {

        InteractionService interactionService = interactionServiceFactory.createMyService("");
        ResponseDTO<InteractionStatisticsDTO> responseDTO = interactionService.calculateStatistics(useId);
        return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
    }
}
