package com.gifa_api.testUnitario;

import com.gifa_api.dto.chofer.AsignarChoferDTO;
import com.gifa_api.dto.chofer.ChoferEditDTO;
import com.gifa_api.dto.chofer.ChoferRegistroDTO;
import com.gifa_api.exception.NotFoundException;
import com.gifa_api.model.Chofer;
import com.gifa_api.model.Vehiculo;
import com.gifa_api.repository.IChoferRepository;
import com.gifa_api.repository.IVehiculoRepository;
import com.gifa_api.service.impl.ChoferServiceImpl;
import com.gifa_api.utils.enums.EstadoChofer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ChoferServiceImplTest {

    @Mock
    private IVehiculoRepository vehiculoRepository;

    @Mock
    private IChoferRepository choferRepository;

    @InjectMocks
    private ChoferServiceImpl choferService;


    @Test
    void habilitarChofer_debeLanzarExcepcionSiNoExiste() {
        // Arrange
        ChoferEditDTO choferEditDTO = new ChoferEditDTO();
        choferEditDTO.setId_chofer(1);

        when(choferRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> choferService.habilitar(choferEditDTO));
        verify(choferRepository, never()).save(any(Chofer.class));
    }


    @Test
    void inhabilitarChofer_debeLanzarExcepcionSiNoExiste() {
        // Arrange
        ChoferEditDTO choferEditDTO = new ChoferEditDTO();
        choferEditDTO.setId_chofer(1);

        when(choferRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> choferService.inhabilitar(choferEditDTO));
        verify(choferRepository, never()).save(any(Chofer.class));
    }

    @Test

    void asignarVehiculo_NoSeEncuentraVehiculoParaElChofer(){
        AsignarChoferDTO asignarChoferDTO = new AsignarChoferDTO(1,1);


        when(vehiculoRepository.findById(asignarChoferDTO.getIdVehiculo())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,() -> choferService.asignarVehiculo(asignarChoferDTO));

        verify(choferRepository,never()).save(any(Chofer.class));
        verify(vehiculoRepository,times(1)).findById(asignarChoferDTO.getIdVehiculo());
    }

    @Test
    void asignarVehiculo_NoSeEncuentraElChofer(){
        AsignarChoferDTO asignarChoferDTO = new AsignarChoferDTO(1,1);

        when(vehiculoRepository.findById(asignarChoferDTO.getIdVehiculo())).thenReturn(Optional.of(new Vehiculo()));
        when(choferRepository.findById(asignarChoferDTO.getIdChofer())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,() -> choferService.asignarVehiculo(asignarChoferDTO));

        verify(vehiculoRepository,times(1)).findById(asignarChoferDTO.getIdVehiculo());
        verify(choferRepository,times(1)).findById(asignarChoferDTO.getIdVehiculo());
        verify(choferRepository,never()).save(any(Chofer.class));
    }


    @Test
    void asignarVehiculo(){
        AsignarChoferDTO asigancionChoferDTO = new AsignarChoferDTO(1,1);
        Vehiculo vehiculo = new Vehiculo();
        Chofer chofer = new Chofer();

        when(vehiculoRepository.findById(asigancionChoferDTO.getIdVehiculo())).thenReturn(Optional.of(vehiculo));
        when(choferRepository.findById(asigancionChoferDTO.getIdChofer())).thenReturn(Optional.of(chofer));

        choferService.asignarVehiculo(asigancionChoferDTO);

        verify(vehiculoRepository,times(1)).findById(asigancionChoferDTO.getIdVehiculo());
        verify(choferRepository,times(1)).findById(asigancionChoferDTO.getIdChofer());
        verify(choferRepository,times(1)).save(any(Chofer.class));
        assertEquals(chofer.getVehiculo(),vehiculo);
    }

    @Test
    void registrarChofer_debeGuardarChoferSiVehiculoExiste() {
        // Arrange
        ChoferRegistroDTO choferRegistroDTO = new ChoferRegistroDTO("juanpe", "123", "Juan Pére");

        // Act
        choferService.registro(choferRegistroDTO);

        // Assert
        verify(choferRepository, times(1)).save(any(Chofer.class));
    }
    @Test
    void habilitarChofer_debeHabilitarSiEstaInhabilitado() {
        // Arrange
        ChoferEditDTO choferEditDTO = new ChoferEditDTO();
        choferEditDTO.setId_chofer(1);

        Chofer chofer = new Chofer();
        chofer.setId(1);
        chofer.setEstadoChofer(EstadoChofer.INHABILITADO);

        when(choferRepository.findById(1)).thenReturn(Optional.of(chofer));

        // Act
        choferService.habilitar(choferEditDTO);

        // Assert
        verify(choferRepository, times(1)).save(any(Chofer.class));
        assertEquals(EstadoChofer.HABILITADO, chofer.getEstadoChofer());
    }
    @Test
    void inhabilitarChofer_debeInhabilitarSiEstaHabilitado() {
        // Arrange
        ChoferEditDTO choferEditDTO = new ChoferEditDTO();
        choferEditDTO.setId_chofer(1);

        Chofer chofer = new Chofer();
        chofer.setId(1);
        chofer.setEstadoChofer(EstadoChofer.HABILITADO);

        when(choferRepository.findById(1)).thenReturn(Optional.of(chofer));

        // Act
        choferService.inhabilitar(choferEditDTO);

        // Assert
        verify(choferRepository, times(1)).save(any(Chofer.class));
        assertEquals(EstadoChofer.INHABILITADO, chofer.getEstadoChofer());
    }

}