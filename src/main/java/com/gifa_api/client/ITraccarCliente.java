package com.gifa_api.client;

import com.gifa_api.dto.traccar.CrearDispositivoResponseDTO;
import com.gifa_api.dto.traccar.DispositivoResponseDTO;
import com.gifa_api.dto.traccar.KilometrosResponseDTO;
import com.gifa_api.dto.traccar.PosicionRequestDTO;
import com.gifa_api.model.Dispositivo;

import java.time.OffsetDateTime;
import java.util.List;

public interface ITraccarCliente {
    public CrearDispositivoResponseDTO postCrearDispositivoTraccar(Dispositivo  dispositivo) ;
    List<PosicionRequestDTO> getPosicionesDispositivoTraccar(Integer deviceId, OffsetDateTime from , OffsetDateTime to);
    List<DispositivoResponseDTO> getDispositivos();
    DispositivoResponseDTO obtenerDispositivoByUniqueId(String uniqueId);
    KilometrosResponseDTO getKilometros(Integer deviceId,OffsetDateTime from, OffsetDateTime to);

}
