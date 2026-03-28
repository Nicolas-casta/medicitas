package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.SpecialtyRequest;
import com.cesde.medicitas.dto.SpecialtyResponse;
import com.cesde.medicitas.entity.Specialty;
import com.cesde.medicitas.exception.DuplicateResourceException;
import com.cesde.medicitas.exception.ResourceNotFoundException;
import com.cesde.medicitas.mapper.SpecialtyMapper;
import com.cesde.medicitas.repository.SpecialtyRepository;
import com.cesde.medicitas.service.impl.SpecialtyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpecialtyService - US-004: CRUD Specialties")
class SpecialtyServiceImplTest {

    @Mock private SpecialtyRepository specialtyRepository;
    @Mock private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty mockSpecialty;
    private SpecialtyRequest validRequest;
    private SpecialtyResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = SpecialtyRequest.builder()
                .name("Cardiology")
                .description("Heart related conditions")
                .build();

        mockSpecialty = Specialty.builder()
                .id(1L)
                .name("Cardiology")
                .description("Heart related conditions")
                .active(true)
                .build();

        mockResponse = SpecialtyResponse.builder()
                .id(1L)
                .name("Cardiology")
                .description("Heart related conditions")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should create specialty successfully")
    void create_WithValidData_ShouldReturnSpecialtyResponse() {
        when(specialtyRepository.existsByNameIgnoreCase("Cardiology")).thenReturn(false);
        when(specialtyMapper.toEntity(validRequest)).thenReturn(mockSpecialty);
        when(specialtyRepository.save(mockSpecialty)).thenReturn(mockSpecialty);
        when(specialtyMapper.toResponse(mockSpecialty)).thenReturn(mockResponse);

        SpecialtyResponse response = specialtyService.create(validRequest);

        assertNotNull(response);
        assertEquals("Cardiology", response.getName());
        verify(specialtyRepository).save(mockSpecialty);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when name already exists")
    void create_WithDuplicateName_ShouldThrowException() {
        when(specialtyRepository.existsByNameIgnoreCase("Cardiology")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> specialtyService.create(validRequest));
        verify(specialtyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return only active specialties")
    void findAllActive_ShouldReturnOnlyActiveSpecialties() {
        when(specialtyRepository.findAllByActiveTrue()).thenReturn(List.of(mockSpecialty));
        when(specialtyMapper.toResponse(mockSpecialty)).thenReturn(mockResponse);

        List<SpecialtyResponse> result = specialtyService.findAllActive();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    @DisplayName("Should update specialty successfully")
    void update_WithValidData_ShouldReturnUpdatedResponse() {
        SpecialtyRequest updateRequest = SpecialtyRequest.builder()
                .name("Neurology")
                .description("Brain and nerves")
                .build();

        when(specialtyRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(mockSpecialty));
        when(specialtyRepository.existsByNameIgnoreCase("Neurology")).thenReturn(false);
        when(specialtyRepository.save(mockSpecialty)).thenReturn(mockSpecialty);
        when(specialtyMapper.toResponse(mockSpecialty)).thenReturn(mockResponse);

        SpecialtyResponse response = specialtyService.update(1L, updateRequest);

        assertNotNull(response);
        verify(specialtyRepository).save(mockSpecialty);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating inactive or missing specialty")
    void update_WithInvalidId_ShouldThrowException() {
        when(specialtyRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> specialtyService.update(99L, validRequest));
    }

    @Test
    @DisplayName("Should deactivate specialty (logical delete)")
    void deactivate_WithValidId_ShouldSetActiveFalse() {
        when(specialtyRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(mockSpecialty));

        specialtyService.deactivate(1L);

        assertFalse(mockSpecialty.isActive());
        verify(specialtyRepository).save(mockSpecialty);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deactivating non-existent specialty")
    void deactivate_WithInvalidId_ShouldThrowException() {
        when(specialtyRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> specialtyService.deactivate(99L));
        verify(specialtyRepository, never()).save(any());
    }
}