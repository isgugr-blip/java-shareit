package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemUpdateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
}
