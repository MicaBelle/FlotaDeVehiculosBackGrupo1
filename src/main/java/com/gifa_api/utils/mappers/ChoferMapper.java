package com.gifa_api.utils.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gifa_api.dto.chofer.ChoferResponseDTO;
import com.gifa_api.model.Chofer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChoferMapper {

    private final ObjectMapper objectMapper;

    @Autowired
    public ChoferMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public ChoferResponseDTO obtenerChoferDTO(Chofer chofer) {
        if (chofer == null) {
            return null;
        }

        ChoferResponseDTO choferResponseDTO = new ChoferResponseDTO();
        choferResponseDTO.setNombre(chofer.getNombre());
        choferResponseDTO.setId_dvehiculo(chofer.getVehiculo() != null ? chofer.getVehiculo().getId() : null);
        choferResponseDTO.setEstadoChofer(chofer.getEstadoChofer().name());

        return choferResponseDTO;
    }


    public List<ChoferResponseDTO> obtenerListaChoferDTO(List<Chofer> choferes) {
        if (choferes == null) {
            return Collections.emptyList();
        }
        return choferes.stream()
                .map(this::obtenerChoferDTO)
                .collect(Collectors.toList());
    }
}
