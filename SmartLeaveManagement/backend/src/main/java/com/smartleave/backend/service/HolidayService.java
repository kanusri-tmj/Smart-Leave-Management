package com.smartleave.backend.service;

import com.smartleave.backend.dto.HolidayRequest;
import com.smartleave.backend.dto.HolidayResponse;
import com.smartleave.backend.entity.Holiday;
import com.smartleave.backend.repository.HolidayRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    @Transactional
    public HolidayResponse create(HolidayRequest request) {
        if (holidayRepository.existsByDate(request.getDate())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A holiday already exists on this date");
        }

        Holiday holiday = new Holiday();
        copyRequest(request, holiday);

        return toResponse(holidayRepository.save(holiday));
    }

    @Transactional(readOnly = true)
    public List<HolidayResponse> getAll() {
        return holidayRepository.findAll().stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public HolidayResponse update(Long id, HolidayRequest request) {
        Holiday holiday = getHoliday(id);

        holidayRepository.findByDate(request.getDate())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "A holiday already exists on this date");
                });

        copyRequest(request, holiday);

        return toResponse(holidayRepository.save(holiday));
    }

    @Transactional
    public void delete(Long id) {
        Holiday holiday = getHoliday(id);
        holidayRepository.delete(holiday);
    }

    private Holiday getHoliday(Long id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Holiday not found"));
    }

    private void copyRequest(HolidayRequest request, Holiday holiday) {
        holiday.setDate(request.getDate());
        holiday.setName(request.getName().trim());
        holiday.setDescription(request.getDescription());
    }

    private HolidayResponse toResponse(Holiday holiday) {
        return new HolidayResponse(
                holiday.getId(),
                holiday.getDate(),
                holiday.getName(),
                holiday.getDescription()
        );
    }
}
