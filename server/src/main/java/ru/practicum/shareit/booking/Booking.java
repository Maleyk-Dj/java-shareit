package ru.practicum.shareit.booking;

import jakarta.persistence.*;
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
@EqualsAndHashCode(of = {"id"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;


    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

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
