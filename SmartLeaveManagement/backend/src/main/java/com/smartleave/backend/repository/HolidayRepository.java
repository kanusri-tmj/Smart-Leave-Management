package com.smartleave.backend.repository;

import com.smartleave.backend.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    Optional<Holiday> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);

    List<Holiday> findByDateBetweenOrderByDateAsc(
            LocalDate startDate, LocalDate endDate);
}
