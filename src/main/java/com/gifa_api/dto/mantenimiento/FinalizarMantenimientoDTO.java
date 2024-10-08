package com.gifa_api.dto.mantenimiento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinalizarMantenimientoDTO {
    List<ItemUtilizadoRequestDTO> items;
}