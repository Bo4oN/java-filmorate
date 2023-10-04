package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class Director {
    @Positive
    private final int id;

    @NotBlank(message = "The director's name cannot be empty.")
    private final String name;
}
