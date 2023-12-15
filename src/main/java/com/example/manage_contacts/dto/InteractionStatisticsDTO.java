package com.example.manage_contacts.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionStatisticsDTO {

    private long totalInteractions;

    private Map<String, Long> interactionTypeCounts ;

    private ContactDTO mostInteractingUser;


}
