package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.*;
import com.enelrith.theelderforge.modlist.dto.projection.ModlistInfo;
import com.enelrith.theelderforge.modlist.dto.projection.ModlistPagedInfo;
import com.enelrith.theelderforge.shared.exception.NotFoundException;
import com.enelrith.theelderforge.shared.exception.NotValidException;
import com.enelrith.theelderforge.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private final CategoryRepository categoryRepository;

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
        var modlist = modlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Modlist not found"));
        if (!modlist.getIsPublic() && !currentUserEmail.equals(modlist.getUser().getEmail())) {
            throw new NotValidException("This modlist is not public");
        }
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
        validateUploadedFile(modlistFile, "modlist.txt");

        var lines = getFileLines(modlistFile);

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
        validateUploadedFile(loadOrderFile, "loadorder.txt");

        var lines = getFileLines(loadOrderFile);

        var pluginNames = getPluginNames(lines);
        int pluginPriorityCount = pluginNames.size() - 1;

        var plugins = buildPlugins(pluginNames, pluginPriorityCount, modlist);
        var savedPlugins = pluginRepository.saveAllAndFlush(plugins);

        log.info("{} plugins added to modlist with id: {}", pluginPriorityCount, modlist.getId());

        return savedPlugins.stream().map(pluginMapper::toPluginDto).toList();
    }

    @Transactional
    public ModlistDto addMetaBuilderInfoToModlist(MultipartFile modDataFile, UUID modlistId, String currentUserEmail) {
        var user = userRepository.findByEmail(currentUserEmail).orElseThrow(()
                -> new NotFoundException("User not found"));
        var modlist = modlistRepository.findByIdAndUser_Id(modlistId, user.getId()).orElseThrow(()
                -> new NotFoundException("Modlist not found"));
        validateUploadedFile(modDataFile, "mod_data.txt");

        var lines = getFileLines(modDataFile);
        var parsedModInfoList = buildParsedModInfoList(lines);

        for (var parsedModInfo : parsedModInfoList) {
            var mod = modRepository.findByNameAndModlist_Id(parsedModInfo.modName(), modlist.getId())
                    .orElseThrow(() -> new NotFoundException("Mod not found"));
            var category = categoryRepository.findByNexusId(parsedModInfo.nexusCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            mod.setCategory(category);
            mod.setNexusId(parsedModInfo.modId());

                for (var pluginName : parsedModInfo.plugins()) {
                var plugin = pluginRepository.findByNameAndModlist_Id(pluginName, modlist.getId())
                        .orElseThrow(() -> new NotFoundException("Plugin not found"));
                plugin.setMod(mod);
                }
        }
        modlistRepository.flush();

        return modlistMapper.toModlistDto(modlist);
    }

    public Page<ModlistPagedInfo> getAllModlists(int page, String name) {
        var pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "updatedAt"));

        return modlistRepository.findAllProjection(pageable, name);
    }

    @Transactional
    public void deleteModlist(UUID modlistId, String userEmail) {
        var modlist = modlistRepository.findByIdAndUser_Email(modlistId, userEmail)
                .orElseThrow(() -> new NotFoundException("Modlist not found"));

        modlistRepository.delete(modlist);
    }

    private List<ParsedModInfo> buildParsedModInfoList(List<String> lines) {
        List<ParsedModInfo> parsedModInfoList = new ArrayList<>();

        for (var line : lines) {
            var splitInfo = line.split("\\|");
            if (splitInfo.length != 0 && splitInfo[0].startsWith("mod_name=")) {
                var cleanedName = splitInfo[0].substring("mod_name=".length());
                var cleanedModId = Integer.valueOf(splitInfo[1].split("modid=")[1]);
                var cleanedNexusCategory = Integer.valueOf(splitInfo[2].split("nexusCategory=")[1]);
                var cleanedPluginList = new ArrayList<String>();

                if (splitInfo.length > 3) {
                    for (int i = 3; i < splitInfo.length; i++) {
                        var cleanedPlugin = splitInfo[i].split("plugin=")[1];
                        cleanedPluginList.add(cleanedPlugin);
                    }
                }
                var parsedModInfo = new ParsedModInfo(cleanedName, cleanedModId, cleanedNexusCategory, cleanedPluginList);
                parsedModInfoList.add(parsedModInfo);
            }
        }
        return parsedModInfoList;
    }

    private List<String> getFileLines(MultipartFile modlistFile) {
        List<String> lines;
        try (var reader = new BufferedReader(new InputStreamReader(modlistFile.getInputStream(), StandardCharsets.UTF_8))) {
            lines = reader.lines().toList();
        } catch (IOException e) {
            throw new NotValidException("Invalid file: " + e.getMessage());
        }
        return lines;
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

    private void validateUploadedFile(MultipartFile file, String expectedFileName) {
        if (file.isEmpty()) {
            throw new NotValidException("File must not be empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.equals(expectedFileName)) {
            throw new NotValidException("Invalid file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals(MediaType.TEXT_PLAIN_VALUE)) {
            throw new NotValidException("Invalid file");
        }
    }
}
