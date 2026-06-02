package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getByOwner_Id(Long ownerId);
    @Query("select i from Item i where i.available is true and (upper(i.name) like upper(concat('%', :query, '%')) or upper(i.description) like upper(concat('%', :query, '%') ))")
    List<Item> findByAvailabilityAndQueryInNameOrDescription(String query);
}
