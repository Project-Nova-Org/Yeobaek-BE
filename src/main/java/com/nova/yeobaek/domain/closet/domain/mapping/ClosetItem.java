package com.nova.yeobaek.domain.closet.domain.mapping;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.shared.BaseEntity;


import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
@Table(name = "closet_items",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_closet_item",
						columnNames = {"closet_id", "item_id"})
		})
public class ClosetItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "closet_id")
	private Closet closet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	// === added ===
	@Column(name = "pos_x", nullable = false)
	private Double posX;

	@Column(name = "pos_y", nullable = false)
	private Double posY;

	@Column(name = "scale", nullable = false)
	private Double scale;

	@Column(name = "rotation", nullable = false)
	private Double rotation;

	@Column(name = "z_index", nullable = false)
	private Integer zIndex;
}