package com.cesde.medicitas.service.ipml;

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
import com.cesde.medicitas.repository.DoctorRepository;
import com.cesde.medicitas.repository.SpecialtyRepository;
import com.cesde.medicitas.repository.UserRepository;
import com.cesde.medicitas.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorResponse create(DoctorRequest req) {
        if (userRepository.existsByEmail(req.email()))
            throw new EmailAlreadyExistsException("Email ya registrado");
        if (doctorRepository.existsByLicenciaMedica(req.licenciaMedica()))
            throw new DuplicateResourceException("Licencia médica ya registrada");

        Specialty specialty = specialtyRepository.findById(req.specialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        User user = User.builder()
                .nombre(req.nombre()).apellido(req.apellido())
                .documento(req.documento()).email(req.email())
                .telefono(req.telefono()).fechaNacimiento(req.fechaNacimiento())
                .password(passwordEncoder.encode(req.documento())) // password temporal
                .role(Role.DOCTOR).activo(true).build();
        userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(user).specialty(specialty)
                .licenciaMedica(req.licenciaMedica()).activo(true).build();
        return toResponse(doctorRepository.save(doctor));
    }

    public List<DoctorResponse> findBySpecialty(Long specialtyId) {
        return doctorRepository.findBySpecialtyIdAndActivoTrue(specialtyId)
                .stream().map(this::toResponse).toList();
    }

    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DoctorResponse update(Long id, DoctorUpdateRequest req) {
        Doctor d = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado: " + id));
        if (req.telefono() != null) d.getUser().setTelefono(req.telefono());
        if (req.licenciaMedica() != null) d.setLicenciaMedica(req.licenciaMedica());
        if (req.specialtyId() != null) {
            Specialty s = specialtyRepository.findById(req.specialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
            d.setSpecialty(s);
        }
        return toResponse(doctorRepository.save(d));
    }

    public void deactivate(Long id) {
        Doctor d = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado: " + id));
        d.setActivo(false);
        doctorRepository.save(d);
    }

    private DoctorResponse toResponse(Doctor d) {
        String nombre = d.getUser().getNombre() + " " + d.getUser().getApellido();
        return new DoctorResponse(d.getId(), nombre, d.getUser().getEmail(),
                d.getUser().getTelefono(), d.getSpecialty().getNombre(),
                d.getLicenciaMedica(), d.isActivo());
    }
}