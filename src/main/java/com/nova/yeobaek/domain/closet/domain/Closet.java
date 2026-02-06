package com.nova.yeobaek.domain.closet.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;
import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.user.domain.User;

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
@Table(
		name = "closets",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_closet_user_name",
						columnNames = {"user_id", "name"}
				)
		}
)
public class Closet extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Builder.Default
	private boolean favorite = false;

	@Column(columnDefinition = "TEXT")
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",nullable = false)
	private User user;

	@OneToMany(mappedBy = "closet", cascade = CascadeType.ALL)
	@Builder.Default
	private List<ClosetItem> closetItemList = new ArrayList<>();
	//옷장생성 메서드
	public static Closet create(User user, String name, String imageUrl) {
		return Closet.builder()
				.user(user)
				.name(name)
				.imageUrl(imageUrl)
				.favorite(false)
				.build();
	}
    //즐겨찾기
	public void toggleFavorite() {
		this.favorite = !this.favorite;
	}
	public void updateFavorite(boolean favorite) {
		this.favorite = favorite;
	}


}
