package com.gifa_api.utils.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gifa_api.dto.mantenimiento.*;
import com.gifa_api.model.Mantenimiento;
import com.gifa_api.model.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MantenimientoMapper {
    private final ObjectMapper objectMapper;

    @Autowired
    public MantenimientoMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MantenimientosResponseDTO mapListToMantenimientosDTO(List<Mantenimiento> mantenimientos) {
        return MantenimientosResponseDTO.builder()
                .mantenimientos(mapToMantenimientosDTO(mantenimientos))
                .build();
    }

    public MantenimientosPendientesResponseDTO mapListToMantenimientosPendientesDTO(List<Mantenimiento> mantenimientos) {
        return MantenimientosPendientesResponseDTO.builder()
                .mantenimientos(mapToMantenimientosDTO(mantenimientos))
                .build();
    }

    private List<MantenimientoResponseDTO> mapToMantenimientosDTO(List<Mantenimiento> mantenimientos) {
        return mantenimientos.stream()
                .map(this::mapToMantenimientoResponseDTO)
                .collect(Collectors.toList());
    }

    private MantenimientoResponseDTO mapToMantenimientoResponseDTO(Mantenimiento mantenimiento) {
        MantenimientoResponseDTO responseDTO = MantenimientoResponseDTO.builder()
                .id(mantenimiento.getId())
                .fechaInicio(mantenimiento.getFechaInicio())
                .fechaFinalizacion(mantenimiento.getFechaFinalizacion())
                .asunto(mantenimiento.getAsunto())
                .repuestoUtilizado(mantenimiento.getRepuestoUtilizado() != null ?
                        mantenimiento.getRepuestoUtilizado().getNombre() : null)
                .estadoMantenimiento(mantenimiento.getEstadoMantenimiento().name())
                .build();

        if (mantenimiento.getOperador() != null) {
            OperadorMantenimientoResponseDTO operadorDTO = OperadorMantenimientoResponseDTO.builder()
                    .id(mantenimiento.getOperador().getId())
                    .usuario(mantenimiento.getOperador().getUsuario())
                    .rol(mantenimiento.getOperador().getRol().toString())
                    .build();
            responseDTO.setOperador(operadorDTO);
        }

        if (mantenimiento.getVehiculo() != null) {
            Vehiculo vehiculo = mantenimiento.getVehiculo();
            VehiculoMantenimientoResponseDTO vehiculoDTO = VehiculoMantenimientoResponseDTO.builder()
                    .id(vehiculo.getId())
                    .patente(vehiculo.getPatente())
                    .antiguedad(vehiculo.getAntiguedad())
                    .kilometraje(vehiculo.getKilometraje())
                    .litrosDeTanque(vehiculo.getLitrosDeTanque())
                    .modelo(vehiculo.getModelo())
                    .estadoVehiculo(vehiculo.getEstadoVehiculo().name())
                    .estadoDeHabilitacion(vehiculo.getEstadoDeHabilitacion().name())
                    .fechaVencimiento(vehiculo.getFechaVencimiento())
                    .build();

            responseDTO.setVehiculo(vehiculoDTO);
        }

        return responseDTO;
    }
}
