package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
}
