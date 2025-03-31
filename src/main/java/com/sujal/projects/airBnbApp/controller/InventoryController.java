package com.sujal.projects.airBnbApp.controller;

import com.sujal.projects.airBnbApp.dto.InventoryDTO;
import com.sujal.projects.airBnbApp.dto.UpdateInventoryRequestDTO;
import com.sujal.projects.airBnbApp.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDTO>> getAllInventoryByRoom(@PathVariable Long roomId) {
        List<InventoryDTO> inventoryDTOList = inventoryService.getAllInventoryByRoom(roomId);
        return ResponseEntity.ok(inventoryDTOList);
    }

    @PatchMapping(path = "/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDTO updateInventoryRequestDTO){
        inventoryService.updateInventory(roomId,updateInventoryRequestDTO);
        return ResponseEntity.noContent().build();
    }


}
