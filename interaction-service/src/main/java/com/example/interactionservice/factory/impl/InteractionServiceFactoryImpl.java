package com.example.interactionservice.factory.impl;

import com.example.interactionservice.constant.InteractionType;
import com.example.interactionservice.factory.InteractionServiceFactory;
import com.example.interactionservice.service.interaction.InteractionService;
import com.example.interactionservice.service.interaction.impl.CallInteractionService;
import com.example.interactionservice.service.interaction.impl.EmailInteractionService;
import com.example.interactionservice.service.interaction.impl.MessageInteractionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InteractionServiceFactoryImpl implements InteractionServiceFactory {


    private final CallInteractionService callInteractionService;

    private final EmailInteractionService emailInteractionService;

    private final MessageInteractionService messageInteractionService;

    private final InteractionService interactionService;


    /**
     * Tạo một dịch vụ tương tác dựa trên loại tương tác được chỉ định.
     *
     * @param type Loại tương tác (CALL, MESSAGE, EMAIL, hoặc các loại khác nếu có).
     * @return Một đối tượng {@code InteractionService} tương ứng với loại tương tác.
     * Nếu loại không được nhận diện, trả về dịch vụ tương tác mặc định.
     */
    @Override
    public InteractionService createMyService(String type) {
        return switch (type.toUpperCase()) {
            case InteractionType.CALL -> callInteractionService;
            case InteractionType.MESSAGE -> messageInteractionService;
            case InteractionType.EMAIL -> emailInteractionService;
            default -> interactionService;
        };
    }
}
