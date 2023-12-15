package com.example.interactionservice.factory;

import com.example.interactionservice.service.interaction.InteractionService;

public interface InteractionServiceFactory {
    InteractionService createMyService(String type);
}
