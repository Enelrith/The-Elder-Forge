package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.modlist.dto.projection.ModlistInfo;
import com.enelrith.theelderforge.modlist.dto.projection.ModlistPagedInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModlistRepository extends JpaRepository<Modlist, UUID> {
    Optional<Modlist> findByIdAndUser_Id(UUID id, UUID userId);
    Optional<Modlist> findByIdAndUser_Email(UUID id, String currentUserEmail);
    List<ModlistInfo> findAllByUser_Email(String currentUserEmail);
    @Query("select m from Modlist m where m.isPublic = true and lower(m.name) like lower(concat('%', :name, '%'))")
    Page<ModlistPagedInfo> findAllProjection(Pageable pageable, @Param("name") String name);

    @Modifying
    @Query(
            value = "delete from modlists where id = :id and user_id = (select id from users where email = :userEmail)",
            nativeQuery = true
    )
    int deleteByIdAndUserEmail(@Param("id") UUID id, @Param("userEmail") String userEmail);
}
