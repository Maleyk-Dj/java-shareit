package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString(exclude = {"item", "booker"})
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @FutureOrPresent
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @NotNull
    @JoinColumn(name = "item_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @JoinColumn(name = "booker_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
