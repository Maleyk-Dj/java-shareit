package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String name;
    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
