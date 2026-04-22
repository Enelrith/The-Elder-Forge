package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.AddModlistRequest;
import com.enelrith.theelderforge.modlist.dto.ModDto;
import com.enelrith.theelderforge.modlist.dto.ModlistDto;
import com.enelrith.theelderforge.modlist.dto.PluginDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/modlists")
public class ModlistController {
    private final ModlistService modlistService;

    @PostMapping
    public ResponseEntity<ModlistDto> addModlist(@RequestBody @Valid AddModlistRequest request,
                                                 Authentication authentication) {
        var modlistDto = modlistService.addModlist(request, authentication.getName());
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(modlistDto.id())
                .toUri();
        return ResponseEntity.created(location).body(modlistDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModlistDto> getModlistById(@PathVariable UUID id, Authentication authentication) {
        var modlistDto = modlistService.getModlistById(id, authentication.getName());

        return ResponseEntity.ok(modlistDto);
    }

    @PostMapping(value = "/{modlistId}/mods/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ModDto>> addModsByFile(@RequestPart MultipartFile modlistFile, @PathVariable UUID modlistId, Authentication authentication) {
        var modsDto = modlistService.addModsByFile(modlistFile, modlistId, authentication.getName());

        return ResponseEntity.ok(modsDto);
    }

    @PostMapping(value = "/{modlistId}/plugins/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PluginDto>> addPluginsByFile(@RequestPart MultipartFile loadOrderFile, @PathVariable UUID modlistId, Authentication authentication) {
        var pluginsDto = modlistService.addPluginsByFile(loadOrderFile, modlistId, authentication.getName());

        return ResponseEntity.ok(pluginsDto);
    }
}
