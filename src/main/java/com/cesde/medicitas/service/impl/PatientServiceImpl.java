package com.cesde.medicitas.service.impl;

import com.cesde.medicitas.dto.PatientPerfilUpdateRequest;
import com.cesde.medicitas.dto.PatientRequest;
import com.cesde.medicitas.dto.PatientResponse;
import com.cesde.medicitas.entity.Patient;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import com.cesde.medicitas.exception.DuplicateResourceException;
import com.cesde.medicitas.exception.EmailAlreadyExistsException;
import com.cesde.medicitas.exception.ResourceNotFoundException;
import com.cesde.medicitas.repository.PatientRepository;
import com.cesde.medicitas.repository.UserRepository;
import com.cesde.medicitas.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── US-009 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public PatientResponse create(PatientRequest req) {
        if (userRepository.existsByEmail(req.email()))
            throw new EmailAlreadyExistsException("Email ya registrado: " + req.email());
        if (userRepository.existsByDocumento(req.documento()))
            throw new DuplicateResourceException("Documento ya registrado: " + req.documento());

        User user = User.builder()
                .nombre(req.nombre())
                .apellido(req.apellido())
                .documento(req.documento())
                .email(req.email())
                .telefono(req.telefono())
                .fechaNacimiento(req.fechaNacimiento())
                .password(passwordEncoder.encode(req.documento()))
                .role(Role.PACIENTE)
                .activo(true)
                .build();
        userRepository.save(user);

        Patient patient = Patient.builder()
                .user(user)
                .direccion(req.direccion())
                .eps(req.eps())
                .tipoSangre(req.tipoSangre())
                .build();

        log.info("Paciente creado: {} {}", user.getNombre(), user.getApellido());
        return toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse findByDocumento(String documento) {
        Patient patient = patientRepository.findByUserDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con documento: " + documento));
        return toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> findAll(String nombre, Pageable pageable) {
        if (nombre != null && !nombre.isBlank())
            return patientRepository.searchByNombre(nombre, pageable).map(this::toResponse);
        return patientRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public PatientResponse update(Long id, PatientRequest req) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));

        User user = patient.getUser();

        if (!user.getEmail().equals(req.email()) && userRepository.existsByEmail(req.email()))
            throw new EmailAlreadyExistsException("Email ya registrado: " + req.email());
        if (!user.getDocumento().equals(req.documento()) && userRepository.existsByDocumento(req.documento()))
            throw new DuplicateResourceException("Documento ya registrado: " + req.documento());

        user.setNombre(req.nombre());
        user.setApellido(req.apellido());
        user.setDocumento(req.documento());
        user.setEmail(req.email());
        user.setTelefono(req.telefono());
        user.setFechaNacimiento(req.fechaNacimiento());
        userRepository.save(user);

        patient.setDireccion(req.direccion());
        patient.setEps(req.eps());
        patient.setTipoSangre(req.tipoSangre());

        return toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!patientRepository.existsById(id))
            throw new ResourceNotFoundException("Paciente no encontrado con id: " + id);
        patientRepository.deleteById(id);
        log.info("Paciente eliminado con id: {}", id);
    }

    // ─── US-010 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getMyProfile(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para usuario: " + userId));
        return toResponse(patient);
    }

    @Override
    @Transactional
    public PatientResponse updateMyProfile(Long userId, PatientPerfilUpdateRequest req) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para usuario: " + userId));

        User user = patient.getUser();
        if (!user.getEmail().equals(req.email()) && userRepository.existsByEmail(req.email()))
            throw new EmailAlreadyExistsException("Email ya registrado: " + req.email());

        user.setEmail(req.email());
        user.setTelefono(req.telefono());
        userRepository.save(user);

        patient.setDireccion(req.direccion());
        return toResponse(patientRepository.save(patient));
    }

    // ─── Mapper ───────────────────────────────────────────────────────────────

    private PatientResponse toResponse(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getUser().getNombre(),
                p.getUser().getApellido(),
                p.getUser().getDocumento(),
                p.getUser().getEmail(),
                p.getUser().getTelefono(),
                p.getUser().getFechaNacimiento(),
                p.getDireccion(),
                p.getEps(),
                p.getTipoSangre()
        );
    }
}