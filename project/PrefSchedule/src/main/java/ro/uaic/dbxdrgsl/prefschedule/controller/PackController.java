package ro.uaic.dbxdrgsl.prefschedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.dbxdrgsl.prefschedule.model.Pack;
import ro.uaic.dbxdrgsl.prefschedule.service.PackService;

import java.util.List;

@RestController
@RequestMapping("/api/packs")
@RequiredArgsConstructor
@Tag(name = "Packs", description = "API for managing course packs")
public class PackController {
    
    private final PackService packService;
    
    @Operation(summary = "Get all packs")
    @GetMapping
    public ResponseEntity<List<Pack>> getAllPacks() {
        return ResponseEntity.ok(packService.findAll());
    }
    
    @Operation(summary = "Get pack by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Pack> getPackById(@PathVariable Long id) {
        return packService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get packs by year")
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Pack>> getPacksByYear(@PathVariable int year) {
        List<Pack> packs = packService.findByYear(year);
        return ResponseEntity.ok(packs);
    }
    
    @Operation(summary = "Get count of packs")
    @GetMapping("/count")
    public ResponseEntity<Long> getPackCount() {
        long count = packService.count();
        return ResponseEntity.ok(count);
    }
}
