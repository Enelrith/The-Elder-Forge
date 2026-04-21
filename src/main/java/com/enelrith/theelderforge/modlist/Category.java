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
@Table(name = "categories")
public class Category extends BaseEntity {
    @Column(nullable = false)
    private Integer nexusId;

    @Column(nullable = false)
    private String name;
}