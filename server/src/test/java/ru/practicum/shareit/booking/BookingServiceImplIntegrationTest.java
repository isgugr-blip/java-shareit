package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService service;
    @Autowired
    private EntityManager em;

    @Test
    void getByBookerAndStateAllReturnsAllUserBookings() {
        User owner = persistUser("owner", "o@example.com");
        User booker = persistUser("booker", "b@example.com");

        Item drill = new Item();
        drill.setName("Drill");
        drill.setDescription("Cordless");
        drill.setAvailable(true);
        drill.setOwner(owner);
        em.persist(drill);

        em.persist(new Booking(null,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2),
                drill, booker, BookingStatus.APPROVED));
        em.persist(new Booking(null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                drill, booker, BookingStatus.WAITING));
        em.flush();

        List<BookingDto> all = service.getByBookerAndState(booker.getId(), BookingState.ALL, 0, 10);
        assertThat(all).hasSize(2);

        List<BookingDto> waiting = service.getByBookerAndState(booker.getId(), BookingState.WAITING, 0, 10);
        assertThat(waiting).hasSize(1);
        assertThat(waiting.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);

        List<BookingDto> past = service.getByBookerAndState(booker.getId(), BookingState.PAST, 0, 10);
        assertThat(past).hasSize(1);
    }

    private User persistUser(String name, String email) {
        User u = new User(null, name, email);
        em.persist(u);
        return u;
    }
}
