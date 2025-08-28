package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(nullable = false)
    private LocalDateTime created;

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
    private List<Item> items;

}
