package com.nova.yeobaek.domain.item.domain.mapping;

import com.nova.yeobaek.domain.item.domain.Color;
import com.nova.yeobaek.domain.item.domain.Item;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
@Table(name = "items_color")
public class ItemsColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 추가하신 독립적인 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;
}
