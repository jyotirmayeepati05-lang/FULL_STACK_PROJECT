package com.example.Backend.dto;

import com.example.Backend.entity.Shift;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public class ShiftResponseDTO {

    private Long id;
    private String name;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private Integer requiredHeadcount;
    private String description;
    private String colorCode;

    public ShiftResponseDTO() {
    }

    public static ShiftResponseDTO fromEntity(Shift shift) {
        if (shift == null) return null;
        ShiftResponseDTO dto = new ShiftResponseDTO();
        dto.setId(shift.getId());
        dto.setName(shift.getName());
        dto.setStartTime(shift.getStartTime());
        dto.setEndTime(shift.getEndTime());
        dto.setRequiredHeadcount(shift.getRequiredHeadcount());
        dto.setDescription(shift.getDescription());
        dto.setColorCode(shift.getColorCode());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
