package com.example.Backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public class ShiftRequestDTO {

    @NotBlank(message = "Shift name is required")
    @Size(max = 100, message = "Shift name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Start time is required (e.g. 08:00)")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required (e.g. 16:00)")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "Required headcount is required")
    @Min(value = 1, message = "Required headcount must be at least 1")
    private Integer requiredHeadcount;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Size(max = 10, message = "Color code cannot exceed 10 characters")
    private String colorCode;

    public ShiftRequestDTO() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getRequiredHeadcount() {
        return requiredHeadcount;
    }

    public void setRequiredHeadcount(Integer requiredHeadcount) {
        this.requiredHeadcount = requiredHeadcount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
