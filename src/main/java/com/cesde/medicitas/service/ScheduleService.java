package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.*;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    ScheduleResponse createSchedule(Long doctorId, ScheduleRequest request);
    List<ScheduleResponse> getSchedulesByDoctor(Long doctorId);
    List<SlotResponse> getAvailableSlots(Long doctorId, LocalDate date);
}