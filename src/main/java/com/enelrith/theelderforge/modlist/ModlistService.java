package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.AddModlistRequest;
import com.enelrith.theelderforge.modlist.dto.ModlistDto;
import com.enelrith.theelderforge.shared.exception.NotFoundException;
import com.enelrith.theelderforge.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ModlistService {
    private final ModlistRepository modlistRepository;
    private final ModlistMapper modlistMapper;
    private final UserRepository userRepository;

    @Transactional
    public ModlistDto addModlist(AddModlistRequest request, String currentUserEmail) {
        var modlist = modlistMapper.toEntity(request);
        var user = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new NotFoundException("User not found"));
        modlist.setUser(user);

        var savedModlist = modlistRepository.saveAndFlush(modlist);
        log.info("Modlist with id: {} created", savedModlist.getId());

        return modlistMapper.toModlistDto(savedModlist);
    }
}
