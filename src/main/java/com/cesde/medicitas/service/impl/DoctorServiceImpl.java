package com.cesde.medicitas.service.impl;

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
import com.cesde.medicitas.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional
    public DoctorResponse create(DoctorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License number already registered: " + request.getLicenseNumber());
        }

        Specialty specialty = specialtyRepository.findByIdAndActiveTrue(request.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", request.getSpecialtyId()));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .identityDocument(request.getIdentityDocument())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthDate(LocalDate.now())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DOCTOR)
                .build();

        User savedUser = userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(savedUser)
                .specialty(specialty)
                .licenseNumber(request.getLicenseNumber())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toResponse(savedDoctor);
    }

    @Override
    public List<DoctorResponse> findAll(Long specialtyId) {
        List<Doctor> doctors = (specialtyId != null)
                ? doctorRepository.findAllBySpecialtyIdAndActiveTrue(specialtyId)
                : doctorRepository.findAllByActiveTrue();

        return doctors.stream()
                .map(doctorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DoctorResponse update(Long id, DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));

        Specialty specialty = specialtyRepository.findByIdAndActiveTrue(request.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", request.getSpecialtyId()));

        boolean licenseChanged = !doctor.getLicenseNumber().equals(request.getLicenseNumber());
        if (licenseChanged && doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License number already registered: " + request.getLicenseNumber());
        }

        User user = doctor.getUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        doctor.setSpecialty(specialty);
        doctor.setLicenseNumber(request.getLicenseNumber());

        Doctor updated = doctorRepository.save(doctor);
        return doctorMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Doctor doctor = doctorRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));

        doctor.setActive(false);
        doctor.getUser().setEnabled(false);
        doctorRepository.save(doctor);
    }
}