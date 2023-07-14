package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class User {
    @PositiveOrZero
    private int id;
    @Email
    private String email;
    private String name;
    @NotNull
    private String login;
    private LocalDate birthday;
}
