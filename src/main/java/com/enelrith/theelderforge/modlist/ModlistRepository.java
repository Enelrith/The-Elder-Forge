package com.enelrith.theelderforge.modlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ModlistRepository extends JpaRepository<Modlist, UUID> {
    Optional<Modlist> findByIdAndUser_Id(UUID id, UUID userId);
    Optional<Modlist> findByIdAndUser_Email(UUID id, String currentUserEmail);
}