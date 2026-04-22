package com.enelrith.theelderforge.modlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PluginRepository extends JpaRepository<Plugin, UUID> {
}