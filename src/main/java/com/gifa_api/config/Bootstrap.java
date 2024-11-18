package com.gifa_api.config;

import com.gifa_api.client.ITraccarCliente;
import com.gifa_api.dto.gestionDeCombustilble.CargaCombustibleRequestDTO;
import com.gifa_api.dto.traccar.DispositivoResponseDTO;
import com.gifa_api.dto.vehiculo.RegistarVehiculoDTO;
import com.gifa_api.model.*;
import com.gifa_api.repository.*;
import com.gifa_api.service.impl.VehiculoServiceImpl;
import com.gifa_api.service.impl.CargaCombustibleServiceImpl;
import com.gifa_api.utils.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class Bootstrap implements ApplicationRunner {
    private final IUsuarioRepository userRepository;
    private final IVehiculoRepository vehiculoRepository;
    private final ITarjetaRepository tarjetaRepository;
    private final ICargaCombustibleRepository cargaCombustibleRepository;
    private final ItemDeInventarioRepository itemDeInventarioRepository;
    private final IPedidoRepository pedidoRepository;
    private final IMantenimientoRepository mantenimientoRepository;
    private final IProveedorRepository proveedorRepository;
    private final IProveedorDeItemRepository proveedorDeParteRepository;
    private final IGestorOperacionalRepository iGestorOperacionalRepository;
    private final IItemUsadoMantenimientoRepository itemUsadoMantenimientoRepository;
    private final IChoferRepository choferRepository;
    private final IDispositivoRepository dispositivoRepository;
    private final ITraccarCliente traccarCliente;
    private final IKilometrajeVehiculoRepository kilometrajeVehiculoRepository;
    private final VehiculoServiceImpl vehiculoServiceImpl;
    private final CargaCombustibleServiceImpl cargaCombustibleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Crear usuarios con builder
        Usuario admin = Usuario.builder()
                .usuario("admin")
                .contrasena("$2a$10$RRAzywJFxaAG3pRlHXep6u6VNKi5KOTT3M8GCxDPHpAyZ0ofX2Bcu")
                .estadoUsuario(EstadoUsuario.HABILITADO)
                .rol(Rol.ADMINISTRADOR)
                .build();

        Usuario operador = Usuario.builder()
                .usuario("operador")
                .contrasena("$2a$10$RRAzywJFxaAG3pRlHXep6u6VNKi5KOTT3M8GCxDPHpAyZ0ofX2Bcu")
                .estadoUsuario(EstadoUsuario.HABILITADO)
                .rol(Rol.OPERADOR)
                .build();

        Usuario supervisor = Usuario.builder()
                .usuario("supervisor")
                .contrasena("$2a$10$RRAzywJFxaAG3pRlHXep6u6VNKi5KOTT3M8GCxDPHpAyZ0ofX2Bcu")
                .estadoUsuario(EstadoUsuario.HABILITADO)
                .rol(Rol.SUPERVISOR)
                .build();

        Usuario gerente = Usuario.builder()
                .usuario("gerente")
                .contrasena("$2a$10$RRAzywJFxaAG3pRlHXep6u6VNKi5KOTT3M8GCxDPHpAyZ0ofX2Bcu")
                .estadoUsuario(EstadoUsuario.HABILITADO)
                .rol(Rol.GERENTE)
                .build();

        userRepository.saveAll(List.of(admin, operador, supervisor, gerente));

        GestorOperacional gestorDePedidos = GestorOperacional.builder()
                .presupuesto(100.00)
                .build();

        iGestorOperacionalRepository.save(gestorDePedidos);



        RegistarVehiculoDTO dto1 = new RegistarVehiculoDTO();
        dto1.setPatente("ABC123");
        dto1.setAntiguedad(5);
        dto1.setKilometrajeUsado(0);
        dto1.setModelo("Modelo X");
        dto1.setFechaRevision(LocalDate.now().plusYears(1));

        RegistarVehiculoDTO dto2 = new RegistarVehiculoDTO();
        dto2.setPatente("XYZ789");
        dto2.setAntiguedad(3);
        dto2.setKilometrajeUsado(0);
        dto2.setModelo("Modelo Y");
        dto2.setFechaRevision(LocalDate.now().plusYears(1));

        vehiculoServiceImpl.registrar(dto1);
        vehiculoServiceImpl.registrar(dto2);

        Vehiculo vehiculo1 = vehiculoRepository.findById(1).orElseThrow();
        Vehiculo vehiculo2 = vehiculoRepository.findById(2).orElseThrow();

        Usuario usuarioChofer = Usuario.builder()
                .usuario("chofer")
                .contrasena("$2a$10$RRAzywJFxaAG3pRlHXep6u6VNKi5KOTT3M8GCxDPHpAyZ0ofX2Bcu")
                .rol(Rol.CHOFER)
                .estadoUsuario(EstadoUsuario.HABILITADO)
                .build();

        Chofer chofer = Chofer.builder()
                .usuario(usuarioChofer)
                .nombre("chofer1")
                .vehiculo(vehiculo1)
                .build();

        choferRepository.saveAll(List.of(chofer));

        // Crear cargas de combustible con builder
        for (int i = 1; i <= 7; i++) {
            OffsetDateTime fechaHora = OffsetDateTime.now().plusDays(i).plusHours(i);
            CargaCombustibleRequestDTO carga = CargaCombustibleRequestDTO.builder()
                    .id(i % 2 == 0 ? 1 : 2)
                    .cantidadLitros(i * 2)
                    .build();
            cargaCombustibleService.cargarCombustible(carga);
        }

        List<DispositivoResponseDTO> dispositivosEnTraccar = traccarCliente.getDispositivos();

        if(dispositivosEnTraccar == null || dispositivosEnTraccar.isEmpty()) {
            List<Dispositivo> dispositivosParaCrear = new ArrayList();

            int cont = 1;
            for(DispositivoResponseDTO dispositivoTraccar : dispositivosEnTraccar) {
                Dispositivo dispositivo = Dispositivo.builder()
                        .unicoId(dispositivoTraccar.getUniqueId())
                        .nombre(dispositivoTraccar.getName())
                        .vehiculo(cont == 1 ? vehiculo1 : vehiculo2)
                        .build();

                dispositivosParaCrear.add(dispositivo);

                if (cont == 1) {
                    vehiculo1.setDispositivo(dispositivo);
                } else {
                    vehiculo2.setDispositivo(dispositivo);
                }

                cont++;
            }

            for(Dispositivo request : dispositivosParaCrear) {
                traccarCliente.postCrearDispositivoTraccar(request);
            }
        }

        //Agrego dispositivos
        vehiculoRepository.saveAll(List.of(vehiculo1, vehiculo2));

        // Crear ítems de inventario con builder
        ItemDeInventario item1 = ItemDeInventario.builder()
                .nombre("Filtro de aceite")
                .umbral(5)
                .stock(10)
                .cantCompraAutomatica(3)
                .build();

        ItemDeInventario item2 = ItemDeInventario.builder()
                .nombre("Neumático")
                .umbral(3)
                .stock(8)
                .cantCompraAutomatica(2)
                .build();

        itemDeInventarioRepository.saveAll(List.of(item1, item2));


        // Crear proveedores con builder y relaciones con ítems
        Proveedor proveedor1 = Proveedor.builder()
                .nombre("Proveedor1")
                .email("proveedor1@email.com")
                .build();

        Proveedor proveedor2 = Proveedor.builder()
                .nombre("Proveedor2")
                .email("proveedor2@email.com")
                .build();

        proveedorRepository.saveAll(List.of(proveedor1, proveedor2));

        ProveedorDeItem proveedorDeItem1 = ProveedorDeItem.builder()

                .proveedor(proveedor1)
                .precio(100.00)
                .itemDeInventario(item2)
                .build();

        ProveedorDeItem proveedorDeItem2 = ProveedorDeItem.builder()
                .proveedor(proveedor2)
                .precio(100.00)
                .itemDeInventario(item1)
                .build();

        proveedorDeParteRepository.saveAll(List.of(proveedorDeItem1, proveedorDeItem2));

        // Crear pedidos con builder
        Pedido pedido1 = Pedido.builder()
                .fecha(LocalDate.now())
                .cantidad(30)
                .motivo("Reponer stock")
                .item(item1)
                .estadoPedido(EstadoPedido.PENDIENTE)
                .build();

        Pedido pedido2 = Pedido.builder()
                .fecha(LocalDate.now())
                .cantidad(20)
                .motivo("Reemplazo de neumático")
                .item(item2)
                .estadoPedido(EstadoPedido.PENDIENTE)
                .build();

        Pedido pedido3 = Pedido.builder()
                .fecha(LocalDate.now())
                .cantidad(15)
                .motivo("Reemplazo de neumático")
                .item(item2)
                .estadoPedido(EstadoPedido.ACEPTADO)
                .build();

        pedidoRepository.saveAll(List.of(pedido1, pedido2,pedido3));


        // Crear mantenimientos con builder
        Mantenimiento mantenimiento1 = Mantenimiento.builder()
                .fechaInicio(LocalDate.now())
                .fechaFinalizacion(LocalDate.now().plusDays(1))
                .asunto("Cambio de aceite")
                .estadoMantenimiento(EstadoMantenimiento.PENDIENTE)
                .operador(operador)
                .vehiculo(vehiculo1)
                .build();

        Mantenimiento mantenimiento2 = Mantenimiento.builder()
                .fechaInicio(LocalDate.now())
                .fechaFinalizacion(LocalDate.now().plusDays(2))
                .asunto("Cambio de neumático")
                .estadoMantenimiento(EstadoMantenimiento.PENDIENTE)
                .operador(operador)
                .vehiculo(vehiculo2)
                .build();

        mantenimientoRepository.saveAll(List.of(mantenimiento1, mantenimiento2));

        //Crear item utilizado en mantenimiento
        ItemUsadoMantenimiento itemUsadoMantenimiento1 = ItemUsadoMantenimiento.builder()
                .mantenimiento(mantenimiento1)
                .itemDeInventario(item1)
                .cantidad(2)
                .build();

        ItemUsadoMantenimiento itemUsadoMantenimiento2 = ItemUsadoMantenimiento.builder()
                .mantenimiento(mantenimiento2)
                .itemDeInventario(item2)
                .cantidad(4)
                .build();

        itemUsadoMantenimientoRepository.saveAll(List.of(itemUsadoMantenimiento1, itemUsadoMantenimiento2));

        // Crear kilometrajes asociados a vehiculo1
        for (int i = 1; i <= 60; i++) {
            OffsetDateTime fechaHora = OffsetDateTime.now().plusDays(i + 5).plusHours(i);
            KilometrajeVehiculo kilometraje = KilometrajeVehiculo.builder()
                    .kilometrosRecorridos(40 + i)
                    .fecha(fechaHora)
                    .vehiculo(i % 2 == 0 ? vehiculo1 : vehiculo2)
                    .build();
            kilometrajeVehiculoRepository.save(kilometraje);
        }
    }
}
