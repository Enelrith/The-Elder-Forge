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
@Table(name = "mods")
public class Mod extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Integer priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modlist_id", nullable = false)
    private Modlist modlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}