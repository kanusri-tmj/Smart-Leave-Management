package com.smartleave.backend.dto;

import java.time.LocalDate;

public class HolidayResponse {

    private Long id;
    private LocalDate date;
    private String name;
    private String description;

    public HolidayResponse(
            Long id, LocalDate date, String name, String description) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
