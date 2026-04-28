package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.*;
import com.enelrith.theelderforge.modlist.dto.projection.ModlistInfo;
import com.enelrith.theelderforge.modlist.dto.projection.ModlistPagedInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        var authName = authentication != null ? authentication.getName() : "";
        var modlistDto = modlistService.getModlistById(id, authName);

        return ResponseEntity.ok(modlistDto);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ModlistInfo>> getAllModlistsByUserEmail(Authentication authentication) {
        var modlists = modlistService.getAllModlistsByUserEmail(authentication.getName());

        return ResponseEntity.ok(modlists);
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

    @PostMapping(value = "/{modlistId}/meta")
    public ResponseEntity<ModlistDto> addMetaBuilderInfoToModlist(@RequestPart MultipartFile modDataFile, @PathVariable UUID modlistId, Authentication authentication) {
        var modlistDto = modlistService.addMetaBuilderInfoToModlist(modDataFile, modlistId, authentication.getName());

        return ResponseEntity.ok(modlistDto);
    }

    @GetMapping
    public ResponseEntity<Page<ModlistPagedInfo>> getAllModlists(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String name) {
        var pagedModlists = modlistService.getAllModlists(page, name);

        return ResponseEntity.ok(pagedModlists);
    }

    @DeleteMapping("/{modlistId}")
    public ResponseEntity<Void> deleteModlist(@PathVariable UUID modlistId, Authentication authentication) {
        modlistService.deleteModlist(modlistId, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{modlistId}")
    public ResponseEntity<ModlistDto> updateModlist(@RequestBody @Valid UpdateModlistRequest request, @PathVariable UUID modlistId, Authentication authentication) {
        var modlistDto = modlistService.updateModlist(request, modlistId, authentication.getName());

        return ResponseEntity.ok(modlistDto);
    }
}
