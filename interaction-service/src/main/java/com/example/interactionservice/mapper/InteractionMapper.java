package com.example.interactionservice.mapper;

import com.example.interactionservice.dto.InteractionDTO;
import com.example.interactionservice.entity.Interaction;

public class InteractionMapper {
    private InteractionMapper() {
    }

    public static Interaction convertToEntity(InteractionDTO interactionDTO) {
        Interaction interaction = new Interaction();
        interaction.setId(interactionDTO.getId());
        interaction.setInteractionType(interactionDTO.getInteraction_type());
        interaction.setInteractionDate(interactionDTO.getInteraction_date());
        interaction.setContactId(interactionDTO.getIdContact());

        return interaction;
    }

    public static InteractionDTO convertToDto(Interaction interaction) {

        InteractionDTO interactionDTO = new InteractionDTO();
        interactionDTO.setId(interaction.getId());
        interactionDTO.setInteraction_date(interaction.getInteractionDate());
        interactionDTO.setInteraction_type(interaction.getInteractionType());
        interactionDTO.setIdContact(interaction.getContactId());
        interactionDTO.setIdUser(interaction.getUserId());

        return interactionDTO;
    }
}
