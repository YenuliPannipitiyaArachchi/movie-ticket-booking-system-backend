package com.ijse.moviebooking.repository;

import com.ijse.moviebooking.entity.*;
import com.ijse.moviebooking.enums.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface ShowRepository extends JpaRepository<Show, Long> {
    boolean existsByTheatreIdAndShowDateAndShowTime(Long theatreId, LocalDate date, LocalTime time);
    boolean existsByTheatreIdAndShowDateAndShowTimeAndIdNot(Long theatreId, LocalDate date, LocalTime time, Long id);

    @Query("select s from Show s where (:movieId is null or s.movie.id = :movieId) "
         + "and (:theatreId is null or s.theatre.id = :theatreId) "
         + "and (:date is null or s.showDate = :date) "
         + "and (:status is null or s.status = :status) order by s.showDate, s.showTime")
    List<Show> search(@Param("movieId") Long movieId, @Param("theatreId") Long theatreId,
                      @Param("date") LocalDate date, @Param("status") ShowStatus status);

    /** Row lock so two customers cannot book the same seat at the same moment. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Show s where s.id = :id")
    Optional<Show> lockById(@Param("id") Long id);
}
