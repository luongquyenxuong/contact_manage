package com.example.interactionservice.service.interaction;

import com.example.interactionservice.dto.InteractionDTO;
import com.example.interactionservice.dto.InteractionStatisticsDTO;
import com.example.interactionservice.dto.ResponseDTO;

import java.util.List;
import java.util.UUID;

public interface InteractionService {
  ResponseDTO<?> saveInteraction (UUID idContact);
  ResponseDTO<?> deleteInteraction (UUID idInteraction);
  ResponseDTO<List<InteractionDTO>> getHistory (UUID userId, UUID contactId, Integer page);
  ResponseDTO<InteractionStatisticsDTO> calculateStatistics (UUID userId);

}
