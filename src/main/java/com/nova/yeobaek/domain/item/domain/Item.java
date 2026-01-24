package com.nova.yeobaek.domain.item.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nova.yeobaek.domain.item.domain.mapping.ItemsColor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;
import com.nova.yeobaek.domain.item.domain.enums.Season;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "items")
public class Item extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String imageUrl;

	private String size;

	private Long price;

	private String memo;

	@Column(name = "brand_name")
	private String brandName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ImageBackgroundColor imageBackgroundColor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "material_id")
	private Material material;

    @Builder.Default
	@ElementCollection(targetClass = Season.class)
	@CollectionTable(name = "seasons", joinColumns = @JoinColumn(name = "item_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "season_name")
	private Set<Season> seasonSet = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemsColor> itemsColors = new ArrayList<>();


	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	@Builder.Default
	private List<ItemUsage> itemUsageList = new ArrayList<>();

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	@Builder.Default
	private List<OOTDItem> ootdItemList = new ArrayList<>();

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	@Builder.Default
	private List<ClosetItem> closetItemList = new ArrayList<>();

	// === Update Methods ===

	public void updateImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void updateImageBackgroundColor(ImageBackgroundColor imageBackgroundColor) {
		this.imageBackgroundColor = imageBackgroundColor;
	}

	public void updateCategory(Category category) {
		this.category = category;
	}

	public void updateMaterial(Material material) {
		this.material = material;
	}

	public void updateBrandName(String brandName) {
		this.brandName = brandName;
	}

	public void updateSize(String size) {
		this.size = size;
	}

	public void updatePrice(Long price) {
		this.price = price;
	}

	public void updateMemo(String memo) {
		this.memo = memo;
	}

	public void updateSeasons(Set<Season> seasons) {
		this.seasonSet.clear();
		this.seasonSet.addAll(seasons);
	}

	public void clearColors() {
		this.itemsColors.clear();
	}
}
