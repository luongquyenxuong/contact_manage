package com.example.interactionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class InteractionRequest {

    @NotBlank(message = "id trống")
    @NotNull
    private UUID idContact;

    @NotBlank(message = "Loại trống")
    @NotNull
    private String type;
}
