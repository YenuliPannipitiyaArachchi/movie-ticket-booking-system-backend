package com.ijse.moviebooking.repository;

import com.ijse.moviebooking.entity.*;
import com.ijse.moviebooking.enums.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("select m from Movie m where (:title is null or lower(m.title) like lower(concat('%', :title, '%'))) "
         + "and (:language is null or lower(m.language) = lower(:language)) "
         + "and (:status is null or m.status = :status) order by m.title")
    List<Movie> search(@Param("title") String title, @Param("language") String language, @Param("status") MovieStatus status);
}
