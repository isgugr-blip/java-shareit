package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = false)
    private LocalDateTime endDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private BookingStatus status;
}
