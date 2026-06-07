package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getByOwner_Id(Long ownerId);

    List<Item> findAllByRequestId_Id(Long requestId);

    List<Item> findAllByRequestId_IdIn(Collection<Long> requestIds);

    @Query("select i from Item i where i.available is true and (upper(i.name) like upper(concat('%', :query, '%')) or upper(i.description) like upper(concat('%', :query, '%') ))")
    List<Item> findByAvailabilityAndQueryInNameOrDescription(String query);
}
