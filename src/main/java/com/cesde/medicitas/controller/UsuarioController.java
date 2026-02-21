package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.UsuarioDTO;
import com.cesde.medicitas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody @Valid UsuarioDTO datos) {

        String token = service.registrarUsuario(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }
}
