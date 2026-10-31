package com.smartleave.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class HolidayRequest {

    @NotNull(message = "Holiday date is required")
    private LocalDate date;

    @NotBlank(message = "Holiday name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 300)
    private String description;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }
}
