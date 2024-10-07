package com.gifa_api.testUnitario;

import com.gifa_api.dto.mantenimiento.*;
import com.gifa_api.exception.BadRoleException;
import com.gifa_api.exception.NotFoundException;
import com.gifa_api.model.*;
import com.gifa_api.repository.IMantenimientoRepository;
import com.gifa_api.repository.IUsuarioRepository;
import com.gifa_api.repository.IVehiculoRepository;
import com.gifa_api.service.impl.MantenimientoServiceImpl;
import com.gifa_api.utils.enums.EstadoMantenimiento;
import com.gifa_api.utils.enums.EstadoVehiculo;
import com.gifa_api.utils.enums.Rol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MantenimientoServiceImplTest {

    @InjectMocks
    private MantenimientoServiceImpl mantenimientoService;

    @Mock
    private IMantenimientoRepository mantenimientoRepository;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IVehiculoRepository vehiculoRepository;

    @Test
    void crearMantenimiento_debeGuardarMantenimiento() {
        // Arrange
        Integer id = 1;
        RegistrarMantenimientoDTO dto = new RegistrarMantenimientoDTO("Cambio de aceite", id);
        Vehiculo vehiculo = new Vehiculo();
        when(vehiculoRepository.findById(dto.getVehiculo_id())).thenReturn(Optional.of(vehiculo));

        mantenimientoService.crearMantenimiento(dto);

        verify(mantenimientoRepository, times(1)).save(any(Mantenimiento.class));
    }

    @Test
    void crearMantenimiento_debeLanzarNotFoundException_siVehiculoNoExiste() {
        // Arrange
        Integer id = 1;
        RegistrarMantenimientoDTO dto = new RegistrarMantenimientoDTO("cambio de aceite", id);
        when(vehiculoRepository.findById(dto.getVehiculo_id())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> mantenimientoService.crearMantenimiento(dto));
    }

    @Test
    void asignarMantenimiento_debeAsignarOperadorYActualizarEstado() {

        AsignarMantenimientoRequestDTO dto = new AsignarMantenimientoRequestDTO(1);
        Mantenimiento mantenimiento = new Mantenimiento();
        Usuario operador = new Usuario();
        operador.setRol(Rol.OPERADOR);

        when(mantenimientoRepository.findById(anyInt())).thenReturn(Optional.of(mantenimiento));
        when(usuarioRepository.findById(dto.getOperadorId())).thenReturn(Optional.of(operador));

        mantenimientoService.asignarMantenimiento(1, dto);


        assertEquals(operador, mantenimiento.getOperador());
        assertEquals(EstadoMantenimiento.APROBADO, mantenimiento.getEstadoMantenimiento());
    }

    @Test
    void asignarMantenimiento_debeLanzarBadRoleException_siUsuarioNoEsOperador() {
        // Arrange
        AsignarMantenimientoRequestDTO dto = new AsignarMantenimientoRequestDTO(1);
        Mantenimiento mantenimiento = new Mantenimiento();
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.ADMINISTRADOR);

        when(mantenimientoRepository.findById(anyInt())).thenReturn(Optional.of(mantenimiento));
        when(usuarioRepository.findById(dto.getOperadorId())).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThrows(BadRoleException.class, () -> mantenimientoService.asignarMantenimiento(1, dto));
    }

    @Test
    void finalizarMantenimiento_debeCambiarEstadoYGuardar() {
        // Arrange
        Mantenimiento mantenimiento = new Mantenimiento();
        Vehiculo vehiculo = new Vehiculo();
        mantenimiento.setVehiculo(vehiculo);

        when(mantenimientoRepository.findById(anyInt())).thenReturn(Optional.of(mantenimiento));

        mantenimientoService.finalizarMantenimiento(1);

        assertEquals(EstadoMantenimiento.FINALIZADO, mantenimiento.getEstadoMantenimiento());
        assertEquals(EstadoVehiculo.REPARADO, vehiculo.getEstadoVehiculo());
        verify(mantenimientoRepository, times(1)).save(mantenimiento);
    }

    @Test
    void finalizarMantenimiento_debeLanzarNotFoundException_siMantenimientoNoExiste() {
        when(mantenimientoRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> mantenimientoService.finalizarMantenimiento(1));
    }
}
