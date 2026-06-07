package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestService service;
    @Autowired
    private EntityManager em;

    @Test
    void getAllByAuthorReturnsRequestsSortedWithItems() {
        User author = persistUser("a", "a@example.com");
        User responder = persistUser("r", "r@example.com");

        ItemRequest older = persistRequest("older", author, LocalDateTime.now().minusDays(2));
        ItemRequest newer = persistRequest("newer", author, LocalDateTime.now().minusHours(1));

        Item answering = new Item();
        answering.setName("Drill");
        answering.setDescription("Cordless");
        answering.setAvailable(true);
        answering.setOwner(responder);
        answering.setRequestId(newer);
        em.persist(answering);
        em.flush();

        List<ItemRequestDto> result = service.getAllByAuthor(author.getId(), 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("newer");
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("Drill");
        assertThat(result.get(0).getItems().get(0).getOwnerId()).isEqualTo(responder.getId());
        assertThat(result.get(1).getItems()).isEmpty();
    }

    private User persistUser(String name, String email) {
        User u = new User(null, name, email);
        em.persist(u);
        return u;
    }

    private ItemRequest persistRequest(String description, User author, LocalDateTime created) {
        ItemRequest r = new ItemRequest(null, description, author, created);
        em.persist(r);
        return r;
    }
}
