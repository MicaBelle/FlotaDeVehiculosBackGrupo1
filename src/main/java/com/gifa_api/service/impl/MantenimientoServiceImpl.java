package com.gifa_api.service.impl;

import com.gifa_api.controller.MantenimientoController;
import com.gifa_api.dto.mantenimiento.*;
import com.gifa_api.exception.BadRoleException;
import com.gifa_api.exception.NotFoundException;
import com.gifa_api.model.*;
import com.gifa_api.repository.IMantenimientoRepository;
import com.gifa_api.repository.IUsuarioRepository;
import com.gifa_api.repository.IVehiculoRepository;
import com.gifa_api.repository.ItemDeInventarioRepository;
import com.gifa_api.service.IMantenimientoService;
import com.gifa_api.service.IitemUsadoMantenimientoService;
import com.gifa_api.utils.enums.EstadoMantenimiento;
import com.gifa_api.utils.enums.EstadoVehiculo;
import com.gifa_api.utils.enums.Rol;
import com.gifa_api.utils.mappers.MantenimientoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MantenimientoServiceImpl implements IMantenimientoService {
    private final IMantenimientoRepository IMantenimientoRepository;
    private final IUsuarioRepository IUsuarioRepository;
    private final MantenimientoMapper mantenimientoMapper;
    private final IVehiculoRepository iVehiculoRepository;

    @Override
    public void crearMantenimiento(RegistrarMantenimientoDTO registrarMantenimientoDTO) {
        Vehiculo vehiculo = iVehiculoRepository.findById(registrarMantenimientoDTO.getVehiculo_id())
                .orElseThrow(() -> new NotFoundException("No se encontró el vehiculo con id: " + registrarMantenimientoDTO.getVehiculo_id()));
        vehiculo.setEstadoVehiculo(EstadoVehiculo.EN_REPARACION);
        Mantenimiento mantenimiento = Mantenimiento
                .builder()
                .asunto(registrarMantenimientoDTO.getAsunto())
                .estadoMantenimiento(EstadoMantenimiento.PENDIENTE)
                .fechaInicio(LocalDate.now())
                .vehiculo(vehiculo)
                .build();

        IMantenimientoRepository.save(mantenimiento);
    }

    @Override
    public MantenimientosResponseDTO verMantenimientosPorVehiculo(Integer id) {
        return mantenimientoMapper.mapListToMantenimientosDTO(IMantenimientoRepository.findByVehiculoId(id));

    }


    @Override
    public MantenimientosResponseDTO verMantenimientos() {
        return mantenimientoMapper.mapListToMantenimientosDTO(IMantenimientoRepository.findAll());
    }

    @Override
    public MantenimientosResponseDTO verMantenimientosPendientes() {
        return mantenimientoMapper.mapListToMantenimientosPendientesDTO(IMantenimientoRepository.findAllByEstadoMantenimiento(EstadoMantenimiento.PENDIENTE));
    }

    @Override
    public MantenimientosResponseDTO verMantenimientosFinalizados() {
        return mantenimientoMapper.mapListToMantenimientosPendientesDTO(IMantenimientoRepository.findAllByEstadoMantenimiento(EstadoMantenimiento.FINALIZADO));
    }

    public void asignarMantenimiento(Integer mantenimientoId, Usuario operador) {
        Mantenimiento mantenimiento = findById(mantenimientoId);

        mantenimiento.setOperador(operador);
        mantenimiento.setEstadoMantenimiento(EstadoMantenimiento.APROBADO);

        IMantenimientoRepository.save(mantenimiento);
    }

    private Mantenimiento findById(Integer mantenimientoId){
        return IMantenimientoRepository.findById(mantenimientoId)
                .orElseThrow(() -> new NotFoundException("No se encontró el mantenimiento con id: " + mantenimientoId));
    }

    @Override
    public void finalizarMantenimiento(Integer mantenimientoId) {
        Mantenimiento mantenimiento = findById(mantenimientoId);
        mantenimiento.setEstadoMantenimiento(EstadoMantenimiento.FINALIZADO);
        mantenimiento.getVehiculo().setEstadoVehiculo(EstadoVehiculo.REPARADO);
                    mantenimiento.setFechaFinalizacion(LocalDate.now());

        IMantenimientoRepository.save(mantenimiento);
    }

    @Override
    public MantenimientosResponseDTO obtenerMantenimientosPorOperador(Integer idOperador) {
        return mantenimientoMapper.mapListToMantenimientosDTO(IMantenimientoRepository.findByOperadorId(idOperador));
    }


}
