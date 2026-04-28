package com.enelrith.theelderforge.modlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PluginRepository extends JpaRepository<Plugin, UUID> {
    Optional<Plugin> findByNameAndModlist_Id(String name, UUID modlistId);
    List<Plugin> findAllByModlist_IdOrderByPriorityAsc(UUID modlistId);

    @Modifying
    @Query("delete from Plugin p where p.modlist.id = :modlistId")
    void deleteAllByModlistId(@Param("modlistId") UUID modlistId);

}
