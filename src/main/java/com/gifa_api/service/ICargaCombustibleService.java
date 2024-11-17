package com.gifa_api.service;

import com.gifa_api.dto.gestionDeCombustilble.CargaCombustibleRequestDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface ICargaCombustibleService {

    void cargarCombustible(CargaCombustibleRequestDTO cargaCombustibleRequestDTO);
    public double combustibleCargadoEntreFechas(Integer numeroTarjeta, LocalDate from ,LocalDate to) ;
}
