package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.AddModlistRequest;
import com.enelrith.theelderforge.modlist.dto.ModlistDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
}
