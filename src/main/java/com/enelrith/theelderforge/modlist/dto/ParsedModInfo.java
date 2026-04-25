package com.enelrith.theelderforge.modlist.dto;

import java.util.List;

public record ParsedModInfo(String modName, Integer modId, Integer nexusCategory, List<String> plugins) {
}
