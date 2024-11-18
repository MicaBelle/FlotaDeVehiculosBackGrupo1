package com.gifa_api.testUnitario.service;

import com.gifa_api.dto.mantenimiento.MantenimientosResponseDTO;
import com.gifa_api.dto.vehiculo.ListaVehiculosResponseDTO;
import com.gifa_api.dto.vehiculo.RegistarVehiculoDTO;
import com.gifa_api.dto.vehiculo.VehiculoResponseConQrDTO;
import com.gifa_api.dto.vehiculo.VehiculoResponseDTO;
import com.gifa_api.exception.BadRequestException;
import com.gifa_api.exception.NotFoundException;
import com.gifa_api.model.Dispositivo;
import com.gifa_api.model.Mantenimiento;
import com.gifa_api.model.Tarjeta;
import com.gifa_api.model.Vehiculo;
import com.gifa_api.repository.ITarjetaRepository;
import com.gifa_api.repository.IVehiculoRepository;
import com.gifa_api.service.ITraccarService;
import com.gifa_api.service.impl.VehiculoServiceImpl;
import com.gifa_api.utils.enums.EstadoDeHabilitacion;
import com.gifa_api.utils.enums.EstadoVehiculo;
import com.gifa_api.utils.mappers.VehiculoMapper;
import com.gifa_api.utils.mappers.VehiculoResponseConQrMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class VehiculoServiceImplTest {

    @Mock
    private IVehiculoRepository vehiculoRepository;

    @Mock
    private ITarjetaRepository tarjetaRepository;

    @Mock
    private VehiculoMapper vehiculoMapper;

    @Mock
    private VehiculoResponseConQrMapper vehiculoResponseConQrMapper;

    @Mock
    private Vehiculo vehiculoFlota;

    @Mock
    private ITraccarService traccarService;

    @InjectMocks
    private VehiculoServiceImpl vehiculoService;

    private Dispositivo dispositivo;

    private RegistarVehiculoDTO vehiculoDTO;

    private Tarjeta tarjeta;

    private Vehiculo vehiculo;
     List<Mantenimiento> mantenimientos;


    @BeforeEach
    void setUp(){
         vehiculoDTO = RegistarVehiculoDTO
                 .builder()
                 .patente("ABC123")
                 .antiguedad(1)
                 .kilometrajeUsado(0)
                 .modelo("toyota")
                 .fechaRevision(LocalDate.now().plusDays(1))
                 .build();

        dispositivo = Dispositivo
                .builder()
                .unicoId(vehiculoDTO.getPatente())
                .nombre("Creacion automatica")
                .build();

         tarjeta = Tarjeta.builder().numero(12345678).build();

         //Integer id = 1;
         vehiculo = Vehiculo.builder()
                 .id(1)
                .patente(vehiculoDTO.getPatente())
                .antiguedad(vehiculoDTO.getAntiguedad())
                .kilometrajeUsado(vehiculoDTO.getKilometrajeUsado())
                .modelo(vehiculoDTO.getModelo())
                .estadoDeHabilitacion(EstadoDeHabilitacion.HABILITADO)
                .estadoVehiculo(EstadoVehiculo.REPARADO)
                .fechaVencimiento(vehiculoDTO.getFechaRevision())
                .tarjeta(tarjeta)
                 .dispositivo(dispositivo)
                 .mantenimientos(Set.of(new Mantenimiento()))
                .build();
    }

    @Test
    void registrar_patenteViejaInvalidaLanzaExcepcion(){
        vehiculoDTO.setPatente("AD1234");
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void registrar_patenteNuevanvalidaLanzaExcepcion(){
        vehiculoDTO.setPatente("AD1821K");
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void registrar_PatenteNulaLanzaExcepcion(){
        vehiculoDTO.setPatente(null);
        verificacionDeNoRegistroDeVehiculoInvalido();
    }
    @Test
    void registrar_antiguedadNoPuedeSerNull(){
        vehiculoDTO.setAntiguedad(null);
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void registrar_antiguedadNoPuedeSerNegativa(){
        vehiculoDTO.setAntiguedad(-1);
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void registrar_kilometrajeNoPuedeSerNull(){
        vehiculoDTO.setKilometrajeUsado(null);
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void registrar_kilometrajeNoPuedeSerNegativa(){
        vehiculoDTO.setKilometrajeUsado(-1);
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void modeloNoPuedeSerVacio(){
        vehiculoDTO.setModelo("");
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void modeloNoPuedeSerNull(){
        vehiculoDTO.setModelo(null);
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void fechaRevisionNoPuedeSerNull(){
        vehiculoDTO.setFechaRevision(null);
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void fechaRevisionNoPuedeSerAnteriorAlaFechaActual(){
        vehiculoDTO.setFechaRevision(LocalDate.now().minusDays(1));
        verificacionDeNoRegistroDeVehiculoInvalido();
    }

    @Test
    void registrarConPatenteVieja() {
        vehiculoService.registrar(vehiculoDTO);
        verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
    }

    @Test
    void testRegistrarConPatenteNueva() {
        vehiculo.setPatente("AB123CD");
        vehiculoService.registrar(vehiculoDTO);
        verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
    }

    @Test
    void testGetVehiculos() {
        ListaVehiculosResponseDTO responseDTO = new ListaVehiculosResponseDTO();
        when(vehiculoMapper.toListaVehiculosResponseDTO(any())).thenReturn(responseDTO);

        ListaVehiculosResponseDTO result = vehiculoService.getVehiculos();

        assertNotNull(result);
        verify(vehiculoMapper, times(1)).toListaVehiculosResponseDTO(any());
    }



    @Test
    void testInhabilitarVehiculoNotFound() {
        Integer vehiculoId = 1;
        when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehiculoService.inhabilitar(vehiculoId));
    }

    @Test
    void testInhabilitarVehiculoQueEstaHabilitado() {
        when(vehiculoRepository.findById(vehiculo.getId())).thenReturn(Optional.of(vehiculo));

        vehiculoService.inhabilitar(vehiculo.getId());

        verify(vehiculoRepository, times(1)).save(vehiculo);
        assertEquals(EstadoDeHabilitacion.INHABILITADO,vehiculo.getEstadoDeHabilitacion());
    }

    @Test
    void habilitarVehiculoNotFound() {
        Integer vehiculoId = 1;
        when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehiculoService.habilitar(vehiculoId));
    }

    @Test
    void testHabilitarVehiculoQueEstaInhabilitado() {
        vehiculo.setEstadoDeHabilitacion(EstadoDeHabilitacion.INHABILITADO);
        when(vehiculoRepository.findById(vehiculo.getId())).thenReturn(Optional.of(vehiculo));

        vehiculoService.habilitar(vehiculo.getId());

        verify(vehiculoRepository, times(1)).save(vehiculo);
        assertEquals(EstadoDeHabilitacion.HABILITADO,vehiculo.getEstadoDeHabilitacion());
    }

    @Test
    void testObtenerHistorialDeVehiculoNotFound() {
        when(vehiculoRepository.findByPatente(vehiculo.getPatente())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehiculoService.obtenerHistorialDeVehiculo(vehiculo.getPatente()));
        verify(vehiculoResponseConQrMapper,never()).toVehiculoResponseConQrDTO(vehiculo,mantenimientos);
    }

    @Test
    void testObtenerHistorialDeVehiculoFound() {
        MantenimientosResponseDTO mantenimientoDTO =   MantenimientosResponseDTO.builder().build();
        VehiculoResponseConQrDTO vehiculoResponseQR =  VehiculoResponseConQrDTO.builder()
                .vehiculo(new VehiculoResponseDTO())
                .mantenimientos(mantenimientoDTO)
                .build();

        when(vehiculoRepository.findByPatente(vehiculo.getPatente())).thenReturn(Optional.of(vehiculo));
        List<Mantenimiento> mantenimientos =  new ArrayList<>(vehiculo.getMantenimientos());

        when(vehiculoResponseConQrMapper.toVehiculoResponseConQrDTO(vehiculo, mantenimientos)).thenReturn(vehiculoResponseQR);

        vehiculoService.obtenerHistorialDeVehiculo(vehiculo.getPatente());
        verify(vehiculoResponseConQrMapper, times(1)).toVehiculoResponseConQrDTO(vehiculo, mantenimientos);
    }

    public void verificacionDeNoRegistroDeVehiculoInvalido(){
        assertThrows(BadRequestException.class,() -> vehiculoService.registrar(vehiculoDTO));
        verify(vehiculoRepository,never()).save(any(Vehiculo.class));
    }
}