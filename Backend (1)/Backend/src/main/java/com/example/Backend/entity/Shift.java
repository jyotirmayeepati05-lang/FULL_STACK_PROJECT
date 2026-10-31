package com.example.Backend.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer requiredHeadcount = 1;

    @Column(length = 255)
    private String description;

    @Column(length = 10)
    private String colorCode = "#3B82F6"; // Default blue color for schedule UI

    public Shift() {
    }

    public Shift(Long id, String name, LocalTime startTime, LocalTime endTime,
                 Integer requiredHeadcount, String description, String colorCode) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.requiredHeadcount = (requiredHeadcount != null) ? requiredHeadcount : 1;
        this.description = description;
        this.colorCode = (colorCode != null && !colorCode.isBlank()) ? colorCode : "#3B82F6";
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
