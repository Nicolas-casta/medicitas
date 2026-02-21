package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.UsuarioDTO;
import com.cesde.medicitas.model.Usuario;
import com.cesde.medicitas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final JwtService jwtService;

    public String registrarUsuario(UsuarioDTO datos) {
        if (repository.existsByEmail(datos.email())) {
            throw new RuntimeException("El email ya esta registrado");
        }
        Usuario nuevoUsuario = new Usuario();
        // Mapeo de datos
        nuevoUsuario.setNombre(datos.nombre());
        nuevoUsuario.setApellido(datos.apellido());
        nuevoUsuario.setDocumentoIdentidad(datos.documentoIdentidad());
        nuevoUsuario.setEmail(datos.email());
        nuevoUsuario.setTelefono(datos.telefono());
        nuevoUsuario.setFechaNacimiento(datos.fechaNacimiento());
        nuevoUsuario.setPassword(datos.password());

        repository.save(nuevoUsuario);

        // Retornar Token JWT
        return jwtService.generateToken(nuevoUsuario);
    }
}
