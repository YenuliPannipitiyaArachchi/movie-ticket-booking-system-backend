package com.ijse.moviebooking.repository;

import com.ijse.moviebooking.entity.*;
import com.ijse.moviebooking.enums.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserEmailOrderByBookingDateDesc(String email);
    List<Booking> findAllByOrderByBookingDateDesc();

    @Query("select seat from Booking b join b.seatNumbers seat where b.show.id = :showId "
         + "and b.status <> com.ijse.moviebooking.enums.BookingStatus.CANCELLED")
    List<String> findBookedSeats(@Param("showId") Long showId);
}
