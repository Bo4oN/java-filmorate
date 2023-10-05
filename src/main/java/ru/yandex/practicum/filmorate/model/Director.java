package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class Director {
    @Positive
    private final int id;

    @NotBlank(message = "The director's name cannot be empty.")
    @Size(max = 30)
    private final String name;
}
