package ru.practicum.shareit.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Objects;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString(exclude = {"owner", "request"})
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String name;
    @NotBlank
    @Column(nullable = false)
    private String description;
    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Item item = (Item) object;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(available, item.available) && Objects.equals(owner, item.owner) && Objects.equals(request, item.request);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
