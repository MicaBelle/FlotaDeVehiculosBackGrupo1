package com.gifa_api.controller;

import com.gifa_api.dto.vehiculo.AsignarParteRequestDTO;
import com.gifa_api.dto.vehiculo.RegistarVehiculoDTO;
import com.gifa_api.model.Vehiculo;
import com.gifa_api.service.IVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehiculo")
@RequiredArgsConstructor
public class VehiculoController {
    private final IVehiculoService vehiculoService;

    @GetMapping("/verAll")
    public List<Vehiculo> verVehiculos() {
        return vehiculoService.getVehiculos();
    }
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody RegistarVehiculoDTO registarVehiculoDTO) {
       vehiculoService.registrar(registarVehiculoDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping ("/inhabilitar/{id}")
    public ResponseEntity<?> inhabilitar(@PathVariable Integer id) {
        vehiculoService.inhabilitar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping ("/habilitar/{id}")
    public ResponseEntity<?> habilitar(@PathVariable Integer id) {
        vehiculoService.habilitar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}