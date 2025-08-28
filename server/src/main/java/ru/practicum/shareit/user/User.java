package ru.practicum.shareit.user;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

}
