package com.smartration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class SlotRequest {

    @NotNull(message = "Slot date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate slotDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Min(value = 1, message = "Capacity must be positive and at least 1")
    private int capacity;

    public SlotRequest() {
    }

    public SlotRequest(LocalDate slotDate, LocalTime startTime, LocalTime endTime, int capacity) {
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
    }

    @JsonFormat(pattern = "HH:mm")
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @JsonSetter("startTime")
    public void setStartTimeFromAny(String value) {
        if (value != null && !value.isBlank()) {
            String trimmed = value.trim();
            this.startTime = (trimmed.length() == 5)
                    ? LocalTime.parse(trimmed + ":00")
                    : LocalTime.parse(trimmed);
        }
    }

    @JsonFormat(pattern = "HH:mm")
    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @JsonSetter("endTime")
    public void setEndTimeFromAny(String value) {
        if (value != null && !value.isBlank()) {
            String trimmed = value.trim();
            this.endTime = (trimmed.length() == 5)
                    ? LocalTime.parse(trimmed + ":00")
                    : LocalTime.parse(trimmed);
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
