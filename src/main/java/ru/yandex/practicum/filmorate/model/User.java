package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@AllArgsConstructor
@Data
public class User {
    @PositiveOrZero
    private int id;
    @Email
    @NotEmpty
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S*")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}
