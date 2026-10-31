package com.smartleave.backend.controller;

import com.smartleave.backend.dto.HolidayRequest;
import com.smartleave.backend.dto.HolidayResponse;
import com.smartleave.backend.service.HolidayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @PostMapping
    public ResponseEntity<HolidayResponse> create(
            @Valid @RequestBody HolidayRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(holidayService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<HolidayResponse>> getAll() {
        return ResponseEntity.ok(holidayService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HolidayResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HolidayRequest request) {
        return ResponseEntity.ok(holidayService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        holidayService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
