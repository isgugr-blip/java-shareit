package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(long userId, ItemRequestCreateDto payload) {
        User author = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ItemRequest saved = itemRequestRepository.save(
                itemRequestMapper.createToEntity(author, LocalDateTime.now(), payload));
        return itemRequestMapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getAllByAuthor(long userId, int from, int size) {
        ensureUserExists(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = itemRequestRepository.findAllByAuthor_Id(userId, page).getContent();
        return mapWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        ensureUserExists(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = itemRequestRepository.findAll(page).getContent().stream()
                .filter(req -> req.getAuthor() == null || req.getAuthor().getId() != userId)
                .toList();
        return mapWithItems(requests);
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        ensureUserExists(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(ItemRequestNotFoundException::new);
        List<Item> items = itemRepository.findAllByRequestId_Id(requestId);
        return itemRequestMapper.toDto(request, items);
    }

    private void ensureUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
    }

    private List<ItemRequestDto> mapWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }
        List<Long> ids = requests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<Item>> byRequest = itemRepository.findAllByRequestId_IdIn(ids).stream()
                .collect(Collectors.groupingBy(item -> item.getRequestId().getId()));
        return requests.stream()
                .map(req -> itemRequestMapper.toDto(req, byRequest.getOrDefault(req.getId(), List.of())))
                .toList();
    }
}
