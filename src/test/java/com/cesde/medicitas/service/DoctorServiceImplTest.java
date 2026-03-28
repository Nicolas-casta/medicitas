package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.DoctorRequest;
import com.cesde.medicitas.dto.DoctorResponse;
import com.cesde.medicitas.dto.DoctorUpdateRequest;
import com.cesde.medicitas.entity.Doctor;
import com.cesde.medicitas.entity.Specialty;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import com.cesde.medicitas.exception.DuplicateResourceException;
import com.cesde.medicitas.exception.EmailAlreadyExistsException;
import com.cesde.medicitas.exception.ResourceNotFoundException;
import com.cesde.medicitas.mapper.DoctorMapper;
import com.cesde.medicitas.repository.DoctorRepository;
import com.cesde.medicitas.repository.SpecialtyRepository;
import com.cesde.medicitas.repository.UserRepository;
import com.cesde.medicitas.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorService - US-005: CRUD Doctors")
class DoctorServiceImplTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private UserRepository userRepository;
    @Mock private SpecialtyRepository specialtyRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private DoctorRequest validRequest;
    private Specialty mockSpecialty;
    private User mockUser;
    private Doctor mockDoctor;
    private DoctorResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = DoctorRequest.builder()
                .firstName("Carlos").lastName("Ruiz")
                .identityDocument("87654321").email("carlos.ruiz@medicitas.com")
                .phone("3009876543").specialtyId(1L)
                .licenseNumber("LIC-001").password("password123")
                .build();

        mockSpecialty = Specialty.builder().id(1L).name("Cardiology").active(true).build();

        mockUser = User.builder().id(10L).firstName("Carlos").lastName("Ruiz")
                .email("carlos.ruiz@medicitas.com").role(Role.DOCTOR).build();

        mockDoctor = Doctor.builder().id(1L).user(mockUser)
                .specialty(mockSpecialty).licenseNumber("LIC-001").active(true).build();

        mockResponse = DoctorResponse.builder().id(1L).userId(10L)
                .firstName("Carlos").lastName("Ruiz")
                .email("carlos.ruiz@medicitas.com")
                .specialtyId(1L).specialtyName("Cardiology")
                .licenseNumber("LIC-001").active(true).build();
    }

    @Test
    @DisplayName("Should create doctor and user with DOCTOR role")
    void create_WithValidData_ShouldReturnDoctorResponse() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(specialtyRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(mockSpecialty));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPwd");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);
        when(doctorMapper.toResponse(mockDoctor)).thenReturn(mockResponse);

        DoctorResponse response = doctorService.create(validRequest);

        assertNotNull(response);
        assertEquals("LIC-001", response.getLicenseNumber());
        verify(userRepository).save(any(User.class));
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is taken")
    void create_WithDuplicateEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> doctorService.create(validRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when license number is taken")
    void create_WithDuplicateLicense_ShouldThrowException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> doctorService.create(validRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when specialty does not exist")
    void create_WithInvalidSpecialty_ShouldThrowException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(specialtyRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.create(validRequest));
    }

    @Test
    @DisplayName("Should return all active doctors when no filter")
    void findAll_WithNoFilter_ShouldReturnAllActiveDoctors() {
        when(doctorRepository.findAllByActiveTrue()).thenReturn(List.of(mockDoctor));
        when(doctorMapper.toResponse(mockDoctor)).thenReturn(mockResponse);

        List<DoctorResponse> result = doctorService.findAll(null);

        assertEquals(1, result.size());
        verify(doctorRepository).findAllByActiveTrue();
    }

    @Test
    @DisplayName("Should filter doctors by specialtyId")
    void findAll_WithSpecialtyFilter_ShouldReturnFilteredDoctors() {
        when(doctorRepository.findAllBySpecialtyIdAndActiveTrue(1L)).thenReturn(List.of(mockDoctor));
        when(doctorMapper.toResponse(mockDoctor)).thenReturn(mockResponse);

        List<DoctorResponse> result = doctorService.findAll(1L);

        assertEquals(1, result.size());
        verify(doctorRepository).findAllBySpecialtyIdAndActiveTrue(1L);
    }

    @Test
    @DisplayName("Should deactivate doctor and disable user account")
    void deactivate_WithValidId_ShouldSetActiveFalse() {
        when(doctorRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(mockDoctor));

        doctorService.deactivate(1L);

        assertFalse(mockDoctor.isActive());
        verify(doctorRepository).save(mockDoctor);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deactivating non-existent doctor")
    void deactivate_WithInvalidId_ShouldThrowException() {
        when(doctorRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.deactivate(99L));
    }
}