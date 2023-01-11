package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp between b.start and b.end")
    Page<Booking> findByBookerIdAndCurrent(long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > current_timestamp order by b.start desc")
    Page<Booking> findByBookerAndStateFuture(long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < current_timestamp order by b.start desc")
    Page<Booking> findByBookerAndStatePast(long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status, Pageable pageable);

    @Query("select b from Booking b where b.item.id in ?1 order by b.start desc")
    Page<Booking> findByItemsAndStateAll(List<Long> itemsIds, Pageable pageable);

    @Query("select b from Booking b where b.item.id in ?1 and b.end < current_timestamp order by b.start desc")
    Page<Booking> findByItemsAndStatePast(List<Long> itemsIds, Pageable pageable);

    @Query("select b from  Booking b where b.item.id in ?1 and current_timestamp between b.start and b.end order by b.start desc")
    Page<Booking> findByItemsAndStateCurrent(List<Long> itemsIds, Pageable pageable);

    @Query("select b from Booking b where b.item.id in ?1 and b.start > current_timestamp order by b.start desc")
    Page<Booking> findByItemsAndStateFuture(List<Long> itemsIds, Pageable pageable);

    @Query("select b from Booking b where b.item.id in ?1 and b.status = ?2")
    Page<Booking> findByItemsAndState(List<Long> itemsIds, Status status, Pageable pageable);

    @Query("select b from Booking b where b.item.id in ?1 and b.end < current_timestamp order by b.start desc")
    List<Booking> getLastBookings(List<Long> itemsIds);

    @Query("select b from Booking b where b.item.id in ?1 and b.start > current_timestamp order by b.start desc")
    List<Booking> getNextBookings(List<Long> itemsIds);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(long bookerId, long itemId, Status status, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime localDateTime);
}
