package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    @PositiveOrZero
    private int id;
    @Email
    private String email;
    private String name;
    @NotBlank
    private String login;
    private LocalDate birthday;
}
