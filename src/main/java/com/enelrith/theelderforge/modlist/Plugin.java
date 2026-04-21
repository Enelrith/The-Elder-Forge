package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "plugins")
public class Plugin extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modlist_id", nullable = false)
    private Modlist modlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_id")
    private Mod mod;
}