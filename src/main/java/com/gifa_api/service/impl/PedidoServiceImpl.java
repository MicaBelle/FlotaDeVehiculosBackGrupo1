package com.gifa_api.service.impl;

import com.gifa_api.dto.proveedoresYPedidos.PedidoResponseDTO;
import com.gifa_api.exception.NotFoundException;
import com.gifa_api.model.*;
import com.gifa_api.repository.IPedidoRepository;
import com.gifa_api.repository.ItemDeInventarioRepository;
import com.gifa_api.service.IGestorOperacionalService;
import com.gifa_api.service.IPedidoService;
import com.gifa_api.service.IProveedorDeItemService;
import com.gifa_api.utils.enums.EstadoPedido;
import com.gifa_api.utils.mappers.PedidosMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {
    private final IPedidoRepository pedidoRepository;
    private final ItemDeInventarioRepository itemDeInventarioRepository;
    private final IGestorOperacionalService gestorOperacionalService;
    private final IProveedorDeItemService proveedorDeItemService;
    private final PedidosMapper pedidosMapper;

    @Override
    public void createPedido(Integer idItem, Integer cantidad,String motivo) {
        ItemDeInventario item = itemDeInventarioRepository.findById(idItem)
                .orElseThrow(() -> new NotFoundException("No se encontró el item con id: " + idItem));
        Pedido pedido = Pedido
                .builder()
                .estadoPedido(EstadoPedido.PENDIENTE)
                .item(item)
                .cantidad(cantidad)
                .fecha(LocalDate.now())
                .motivo(motivo)
                .build();
        pedidoRepository.save(pedido);

    }



    @Scheduled(fixedRate = 86400000)
    public void hacerPedidos() {

        List<ItemDeInventario> itemsDeInventario = itemDeInventarioRepository.findAll();
        GestorOperacional  gestorOperacional  = gestorOperacionalService.getGestorOperacional();

        for (ItemDeInventario item : itemsDeInventario) {
            if (item.getUmbral() > item.getStock()) {
                int cantidad = item.getCantCompraAutomatica() + item.getUmbral();

                ProveedorDeItem proveerDeItemMasEconomico = proveedorDeItemService.proveedorMasEconomico(item.getId());
                if ((proveerDeItemMasEconomico.getPrecio() * cantidad) < gestorOperacional.getPresupuesto()) {

                    if (!existeElPedidoByItemId(item.getId())) {
                        createPedido(item.getId(), cantidad, "Solcitud de stock automatica");
                    }

                }
            }
        }

    }

    public boolean existeElPedidoByItemId(Integer idItem) {
        return pedidoRepository.existsByItemId(idItem);
    }

    @Override
    public List<PedidoResponseDTO> obtenerPedidos() {
       return  pedidosMapper.mapToPedidoDTO(pedidoRepository.findAll());
    }
}



