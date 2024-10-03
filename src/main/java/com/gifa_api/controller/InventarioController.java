package com.gifa_api.controller;

import com.gifa_api.dto.RegistrarItemDeInventarioDTO;
import com.gifa_api.service.IItemDeIventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class InventarioController {
    private final IItemDeIventarioService itemDeIventarioService;

    @PostMapping("/registrarItem")
    public ResponseEntity<?> registrarItem(@RequestBody RegistrarItemDeInventarioDTO registrarItemDeInventarioDTO){
        itemDeIventarioService.registrar(registrarItemDeInventarioDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/utilizarItem/{id}")
    public ResponseEntity<?> utilizarItem(@PathVariable Integer id){
        itemDeIventarioService.utilizarItem(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}