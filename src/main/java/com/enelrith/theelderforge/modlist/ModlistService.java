package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.AddModlistRequest;
import com.enelrith.theelderforge.modlist.dto.ModDto;
import com.enelrith.theelderforge.modlist.dto.ModlistDto;
import com.enelrith.theelderforge.modlist.dto.PluginDto;
import com.enelrith.theelderforge.modlist.dto.projection.ModlistInfo;
import com.enelrith.theelderforge.shared.exception.NotFoundException;
import com.enelrith.theelderforge.shared.exception.NotValidException;
import com.enelrith.theelderforge.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ModlistService {
    private final ModlistRepository modlistRepository;
    private final ModlistMapper modlistMapper;
    private final UserRepository userRepository;
    private final ModMapper modMapper;
    private final ModRepository modRepository;
    private final PluginRepository pluginRepository;
    private final PluginMapper pluginMapper;

    @Transactional
    public ModlistDto addModlist(AddModlistRequest request, String currentUserEmail) {
        var modlist = modlistMapper.toEntity(request);
        var user = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new NotFoundException("User not found"));
        modlist.setUser(user);

        var savedModlist = modlistRepository.saveAndFlush(modlist);
        log.info("Modlist with id: {} created", savedModlist.getId());

        return modlistMapper.toModlistDto(savedModlist);
    }

    public ModlistDto getModlistById(UUID id, String currentUserEmail) {
        var modlist = modlistRepository.findByIdAndUser_Email(id, currentUserEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return modlistMapper.toModlistDto(modlist);
    }

    public List<ModlistInfo> getAllModlistsByUserEmail(String currentUserEmail) {
        return modlistRepository.findAllByUser_Email(currentUserEmail);
    }

    @Transactional
    public List<ModDto> addModsByFile(MultipartFile modlistFile, UUID modlistId, String currentUserEmail) {
        var user = userRepository.findByEmail(currentUserEmail).orElseThrow(()
                -> new NotFoundException("User not found"));
        var modlist = modlistRepository.findByIdAndUser_Id(modlistId, user.getId()).orElseThrow(()
                -> new NotFoundException("Modlist not found"));
        validateModlistFile(modlistFile);

        List<String> lines;
        try (var reader = new BufferedReader(new InputStreamReader(modlistFile.getInputStream(), StandardCharsets.UTF_8))) {
            lines = reader.lines().toList();
        } catch (IOException e) {
            throw new NotValidException("Invalid file: " + e.getMessage());
        }

        var modNames = getModNames(lines);
        int modPriorityCount = modNames.size() - 1;

        var mods = buildMods(modNames, modPriorityCount, modlist);
        var savedMods = modRepository.saveAllAndFlush(mods);

        log.info("{} mods added to modlist with id: {}", modPriorityCount, modlist.getId());

        return savedMods.stream().map(modMapper::toModDto).toList();
    }

    @Transactional
    public List<PluginDto> addPluginsByFile(MultipartFile loadOrderFile, UUID modlistId, String currentUserEmail) {
        var user = userRepository.findByEmail(currentUserEmail).orElseThrow(()
                -> new NotFoundException("User not found"));
        var modlist = modlistRepository.findByIdAndUser_Id(modlistId, user.getId()).orElseThrow(()
                -> new NotFoundException("Modlist not found"));
        validateLoadOrderFile(loadOrderFile);

        List<String> lines;
        try (var reader = new BufferedReader(new InputStreamReader(loadOrderFile.getInputStream(), StandardCharsets.UTF_8))) {
            lines = reader.lines().toList();
        } catch (IOException e) {
            throw new NotValidException("Invalid file: " + e.getMessage());
        }

        var pluginNames = getPluginNames(lines);
        int pluginPriorityCount = pluginNames.size() - 1;

        var plugins = buildPlugins(pluginNames, pluginPriorityCount, modlist);
        var savedPlugins = pluginRepository.saveAllAndFlush(plugins);

        log.info("{} plugins added to modlist with id: {}", pluginPriorityCount, modlist.getId());

        return savedPlugins.stream().map(pluginMapper::toPluginDto).toList();
    }

    private List<String> getModNames(List<String> lines) {
        List<String> modNames = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("+") || line.startsWith("*")) {
                String modName = line.substring(1);
                modNames.add(modName);
            }
        }
        return modNames;
    }

    private List<String> getPluginNames(List<String> lines) {
        List<String> pluginNames = new ArrayList<>();
        for (String line : lines) {
            if (line.endsWith(".esp") || line.endsWith(".esm") || line.endsWith(".esl")) {
                pluginNames.add(line);
            }
        }
        return pluginNames;
    }

    private List<Mod> buildMods(List<String> modNames, int modCount, Modlist modlist) {
        List<Mod> mods = new ArrayList<>();
        for (String modName : modNames) {
            var mod = new Mod();
            mod.setName(modName);
            mod.setPriority(modCount);
            mod.setModlist(modlist);
            mods.add(mod);
            modCount--;
        }
        return mods;
    }

    private List<Plugin> buildPlugins(List<String> pluginNames, int pluginCount, Modlist modlist) {
        List<Plugin> plugins = new ArrayList<>();
        int index = 0;
        for (String pluginName : pluginNames) {
            var plugin = new Plugin();
            plugin.setName(pluginName);
            plugin.setPriority(index);
            plugin.setModlist(modlist);
            plugins.add(plugin);
            index++;
        }
        return plugins;
    }

    private void validateModlistFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new NotValidException("File must not be empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.equals("modlist.txt")) {
            throw new NotValidException("Invalid file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals(MediaType.TEXT_PLAIN_VALUE)) {
            throw new NotValidException("Invalid file");
        }
    }

    private void validateLoadOrderFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new NotValidException("File must not be empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.equals("loadorder.txt")) {
            throw new NotValidException("Invalid file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals(MediaType.TEXT_PLAIN_VALUE)) {
            throw new NotValidException("Invalid file");
        }
    }
}
