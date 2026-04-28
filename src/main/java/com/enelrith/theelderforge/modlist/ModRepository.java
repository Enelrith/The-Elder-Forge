package com.enelrith.theelderforge.modlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModRepository extends JpaRepository<Mod, UUID> {
    Optional<Mod> findByNameAndModlist_Id(String name, UUID modlistId);
    List<Mod> findAllByModlist_IdOrderByPriorityDesc(UUID modlistId);

    @Modifying
    @Query("delete from Mod m where m.modlist.id = :modlistId")
    void deleteAllByModlistId(@Param("modlistId") UUID modlistId);

}
