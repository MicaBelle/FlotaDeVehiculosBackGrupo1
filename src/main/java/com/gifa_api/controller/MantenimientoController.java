package com.gifa_api.controller;

import com.gifa_api.dto.mantenimiento.AsignarMantenimientoRequestDTO;
import com.gifa_api.dto.mantenimiento.MantenimientosPendientesResponseDTO;
import com.gifa_api.dto.mantenimiento.MantenimientosResponseDTO;
import com.gifa_api.dto.mantenimiento.RegistrarMantenimientoDTO;
import com.gifa_api.model.Mantenimiento;
import com.gifa_api.service.IMantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mantenimiento")
@RequiredArgsConstructor
public class MantenimientoController {
    private final IMantenimientoService mantenimientoService;

    @PostMapping("/crear-manualmente")
    public ResponseEntity<?> cargarMantenimientoManualmente(@RequestBody RegistrarMantenimientoDTO registrarMantenimientoDTO){
        mantenimientoService.crearMantenimiento(registrarMantenimientoDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("por-vehiculo/{id}")
    public List<Mantenimiento> verMantenimientoPorVehiculo(@PathVariable Integer id){
        return mantenimientoService.verMantenimientosPorVehiculo(id);
    }

    @GetMapping("/")
    public ResponseEntity<MantenimientosResponseDTO> verMantenimientos(){
        return new ResponseEntity<>(mantenimientoService.verMantenimientos(), HttpStatus.OK);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<MantenimientosPendientesResponseDTO> verMantenimientosPendientes(){
        return new ResponseEntity<>(mantenimientoService.verMantenimientosPendientes(), HttpStatus.OK);
    }

    @PatchMapping("/asignar/{mantenimientoId}")
    public ResponseEntity<?> asignarMantenimiento(@PathVariable Integer mantenimientoId,
                                                  @RequestBody AsignarMantenimientoRequestDTO asignarMantenimientoRequestDTO){
        mantenimientoService.asignarMantenimiento(mantenimientoId, asignarMantenimientoRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/finalizar/{mantenimientoId}")
    public ResponseEntity<?> finalizarMantenimiento(@PathVariable Integer mantenimientoId){
        mantenimientoService.finalizarMantenimiento(mantenimientoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
