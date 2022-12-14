package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp between b.start and b.end")
    List<Booking> findByBookerIdAndCurrent(long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > current_timestamp order by b.start desc")
    List<Booking> findByBookerAndStateFuture(long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < current_timestamp order by b.start desc")
    List<Booking> findByBookerAndStatePast(long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    @Query("select b from Booking b where b.item.id in ?1 order by b.start desc")
    List<Booking> findByItemsAndStateAll(List<Long> itemsIds);

    @Query("select b from Booking b where b.item.id in ?1 and b.end < current_timestamp order by b.start desc")
    List<Booking> findByItemsAndStatePast(List<Long> itemsIds);

    @Query("select b from  Booking b where b.item.id in ?1 and current_timestamp between b.start and b.end order by b.start desc")
    List<Booking> findByItemsAndStateCurrent(List<Long> itemsIds);

    @Query("select b from Booking b where b.item.id in ?1 and b.start > current_timestamp order by b.start desc")
    List<Booking> findByItemsAndStateFuture(List<Long> itemsIds);

    @Query("select b from Booking b where b.item.id in ?1 and b.status = ?2")
    List<Booking> findByItemsAndState(List<Long> itemsIds, Status status);

    @Query("select b from Booking b where b.item.id = ?1 and b.end < current_timestamp order by b.end desc")
    List<Booking> getLastBookingsById(long itemId);

    @Query("select b from Booking b where b.item.id = ?1 and b.start > current_timestamp order by b.start asc")
    List<Booking> getNextBookingsById(long itemId);

    @Query("select b from Booking b where b.item.id in ?1 and b.end < current_timestamp order by b.start desc")
    List<Booking> getLastBookings(List<Long> itemsIds);

    @Query("select b from Booking b where b.item.id in ?1 and b.start > current_timestamp order by b.start desc")
    List<Booking> getNextBookings(List<Long> itemsIds);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(long bookerId, long itemId, Status status, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime localDateTime);
}
