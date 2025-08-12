package ru.practicum.shareit.comment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false, length = 1000)
    private String text;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @Column(name = "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Comment comment = (Comment) object;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
