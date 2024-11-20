package com.gifa_api.service;

import com.gifa_api.dto.traccar.InconsistenciasKMconCombustiblesResponseDTO;
import com.gifa_api.dto.traccar.DispositivoResponseDTO;
import com.gifa_api.dto.traccar.KilometrosResponseDTO;
import com.gifa_api.dto.traccar.PosicionResponseDTO;
import com.gifa_api.model.Dispositivo;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface ITraccarService {

    void crearDispositivo(Dispositivo dispositivo);

    List<DispositivoResponseDTO> obtenerDispositivos();

    List<InconsistenciasKMconCombustiblesResponseDTO> getInconsistencias(OffsetDateTime from, OffsetDateTime to);

    KilometrosResponseDTO getKilometros(Integer deviceId, OffsetDateTime from, OffsetDateTime to);
    List<PosicionResponseDTO> obtenerPosicionesEnVivo(String uniqueId);
    List<PosicionResponseDTO> obtenerPosicionesEnRangoDeFechas(String uniqueId, OffsetDateTime from,OffsetDateTime to);

}
