package ru.practicum.shareit.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdAndStatus(@Param("bookerId") Long bookerId, @Param("status") Status status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > :currentDate ORDER BY b.start ASC")
    List<Booking> findAllByBookerIdAndFuture(@Param("bookerId") Long bookerId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :currentDate ORDER BY b.end ASC")
    List<Booking> findAllByBookerIdAndPast(@Param("bookerId") Long bookerId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.item IN :items ORDER BY b.start DESC")
    List<Booking> findByAllItems(@Param("items") List<Item> items);

    @Query("SELECT b FROM Booking b WHERE b.item IN :items AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByItemsAndStatus(@Param("items") List<Item> items, @Param("status") Status status);

    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.status = 'APPROVED' AND b.start < :currentDate AND b.end > :currentDate AND b.item.id = :itemId ORDER BY b.end ASC")
    List<Booking> findLastBooking(@Param("itemId") Long itemId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.status = 'APPROVED' AND b.start > :currentDate AND b.item.id = :itemId ORDER BY b.start ASC")
    List<Booking> findNextBooking(@Param("itemId") Long itemId, @Param("currentDate") LocalDateTime currentDate);
}
