package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService service;
    @Autowired
    private EntityManager em;

    @Test
    void getByUserReturnsItemsWithBookingsAndComments() {
        User owner = persistUser("owner", "owner@example.com");
        User booker = persistUser("booker", "booker@example.com");

        Item drill = new Item();
        drill.setName("Drill");
        drill.setDescription("Cordless drill");
        drill.setAvailable(true);
        drill.setOwner(owner);
        em.persist(drill);

        Booking past = new Booking(null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                drill, booker, BookingStatus.APPROVED);
        em.persist(past);

        Booking future = new Booking(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                drill, booker, BookingStatus.APPROVED);
        em.persist(future);

        em.flush();

        List<ItemDto> result = service.getByUser(owner.getId());

        assertThat(result).hasSize(1);
        ItemDto item = result.get(0);
        assertThat(item.getName()).isEqualTo("Drill");
        assertThat(item.getLastBooking()).isNotNull();
        assertThat(item.getLastBooking().getBookerId()).isEqualTo(booker.getId());
        assertThat(item.getNextBooking()).isNotNull();
        assertThat(item.getNextBooking().getBookerId()).isEqualTo(booker.getId());
    }

    private User persistUser(String name, String email) {
        User u = new User(null, name, email);
        em.persist(u);
        return u;
    }
}
