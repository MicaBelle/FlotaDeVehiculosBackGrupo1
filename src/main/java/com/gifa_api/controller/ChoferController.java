package com.gifa_api.controller;

import com.gifa_api.dto.chofer.AsignarChoferDTO;
import com.gifa_api.dto.chofer.ChoferEditDTO;
import com.gifa_api.dto.chofer.ChoferRegistroDTO;
import com.gifa_api.exception.BadRoleException;
import com.gifa_api.exception.NotFoundException;
import com.gifa_api.model.Chofer;
import com.gifa_api.model.Usuario;
import com.gifa_api.service.IChoferService;
import com.gifa_api.utils.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chofer")
@RequiredArgsConstructor
public class ChoferController {
    private final IChoferService choferService;

    @PostMapping("/registrar")
    public ResponseEntity<?> createChofer(@RequestBody ChoferRegistroDTO choferRegistroDTO) {
        choferService.registro(choferRegistroDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PatchMapping("/asignarChofer")
    public ResponseEntity<?> AsignarChofer(@RequestBody AsignarChoferDTO asignarChoferDTO) {
        choferService.asignarVehiculo(asignarChoferDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/habilitar")
    public ResponseEntity<?> habilitar(@RequestBody ChoferEditDTO choferEditDTO) {
        choferService.habilitar(choferEditDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PatchMapping("/inhabilitar")
    public ResponseEntity<?> createChofer(@RequestBody ChoferEditDTO choferEditDTO) {
        choferService.inhabilitar(choferEditDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verChofers")
    public ResponseEntity<?> getAllChofers() {
        return new ResponseEntity<>(choferService.obtenerAll(),HttpStatus.OK);
    }
}
