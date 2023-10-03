package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.PositiveOrZero;

@Data
@RequiredArgsConstructor
public class Entity {
    @PositiveOrZero
    private int id;
}
