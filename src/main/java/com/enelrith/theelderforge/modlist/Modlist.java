package com.enelrith.theelderforge.modlist;

import com.enelrith.theelderforge.shared.BaseEntity;
import com.enelrith.theelderforge.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "modlists")
public class Modlist extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(length = 5000)
    private String description;

    @Column(nullable = false)
    private Boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "modlist", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Mod> mods = new ArrayList<>();

    @OneToMany(mappedBy = "modlist", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Plugin> plugins = new ArrayList<>();
}