package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.projection.ModlistInfo;
import com.enelrith.theelderforge.modlist.dto.projection.ModlistPagedInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModlistRepository extends JpaRepository<Modlist, UUID> {
    Optional<Modlist> findByIdAndUser_Id(UUID id, UUID userId);
    Optional<Modlist> findByIdAndUser_Email(UUID id, String currentUserEmail);
    List<ModlistInfo> findAllByUser_Email(String currentUserEmail);
    @Query("select m from Modlist m where m.isPublic = true")
    Page<ModlistPagedInfo> findAllProjection(Pageable pageable);
}