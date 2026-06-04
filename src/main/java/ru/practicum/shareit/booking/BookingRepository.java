package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdAndItem_Owner_Id(Long id, Long ownerId);

    Page<Booking> findAllByBooker_Id(Long id, Pageable pageable);

    Page<Booking> findAllByEndDateIsBeforeAndBooker_Id(LocalDateTime date, Long id,
                                                       Pageable pageable);

    Page<Booking> findAllByStartDateIsAfterAndBooker_Id(LocalDateTime date, Long id,
                                                        Pageable pageable);

    Page<Booking> findAllByStartDateIsBeforeAndEndDateIsAfterAndBooker_Id(LocalDateTime dateTime,
                                                                          LocalDateTime dateTime2, Long id,
                                                                          Pageable pageable);

    Page<Booking> findAllByStatusAndBooker_Id(BookingStatus status, Long id,
                                              Pageable pageable);

    Page<Booking> findAllByItem_Owner_Id(Long id, Pageable pageable);

    Page<Booking> findAllByEndDateIsBeforeAndItem_Owner_Id(LocalDateTime dateTime, Long id, Pageable pageable);

    Page<Booking> findAllByStartDateIsAfterAndItem_Owner_Id(LocalDateTime date, Long id,
                                                        Pageable pageable);

    Page<Booking> findAllByStartDateIsBeforeAndEndDateIsAfterAndItem_Owner_Id(LocalDateTime dateTime,
                                                                          LocalDateTime dateTime2, Long id,
                                                                          Pageable pageable);

    Page<Booking> findAllByStatusAndItem_Owner_Id(BookingStatus status, Long id,
                                              Pageable pageable);

    Optional<Booking> findFirstByEndDateIsBeforeAndStatusAndItem_Owner_Id(LocalDateTime dateTime, BookingStatus status, Long id);

    Optional<Booking> findFirstByStartDateIsAfterAndStatusAndItem_Owner_Id(LocalDateTime startDate,
                                                                           BookingStatus status, Long itemOwnerId);

    Optional<Booking> findFirstByItem_IdAndStatusAndEndDateIsBeforeOrderByEndDateDesc(
            Long itemId, BookingStatus status, LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(
            Long itemId, BookingStatus status, LocalDateTime now);

    boolean existsByBooker_IdAndItem_IdAndEndDateIsBeforeAndStatus(
            Long bookerId, Long itemId, LocalDateTime now, BookingStatus status);
}
