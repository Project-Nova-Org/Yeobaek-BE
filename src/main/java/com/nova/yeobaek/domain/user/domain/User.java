package com.nova.yeobaek.domain.user.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.nova.yeobaek.domain.user.domain.enums.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "users",
		uniqueConstraints = {
			@UniqueConstraint(
				name = "uk_oauth_provider_id",
				columnNames = {"oauth_provider", "oauth_id"})
})
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OauthProvider oauthProvider;

	@Column(nullable = false)
	private String oauthId;

	private String email;

	@Column(unique = true)
	private String nickname;

	@Column(columnDefinition = "TEXT")
	private String profileImageUrl;

	@Column(columnDefinition = "TEXT")
	private String bodyImageUrl;

	private float weight;

	private float height;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR DEFAULT 'LV1'")
	private Rank rank;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR DEFAULT 'ACTIVE'")
	private UserStatus status;

	private LocalDateTime inactiveDate;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
	private long fittingCount;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Closet> closetList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Item> itemList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Builder.Default
	private List<OOTD> ootdList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Builder.Default
	private List<ItemUsage> itemUsageList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Calendar> calendarList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Builder.Default
	private List<DeviceToken> deviceTokenList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@Builder.Default
	private List<UserHistory> userHistoryList = new ArrayList<>();

	public static User createSocialUser(
			OauthProvider oauthProvider,
			String oauthId,
			Role role
	) {
		Objects.requireNonNull(oauthProvider, "oauthProvider는 필수");
		Objects.requireNonNull(oauthId, "oauthId는 필수");
		Objects.requireNonNull(role, "role은 필수");

		User user = new User();
		user.oauthProvider = oauthProvider;
		user.oauthId = oauthId;
		user.role = role;
		return user;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updatePreference(
			float height,
			float weight,
			Gender gender,
			String bodyImageUrl
	) {
		this.height = height;
		this.weight = weight;
		if (gender != null) this.gender = gender;
		if (bodyImageUrl != null) this.bodyImageUrl = bodyImageUrl;
	}

	// 탈퇴 처리
	public void withdraw() {
		this.status = UserStatus.DELETED;
		this.inactiveDate = LocalDateTime.now();
	}

	// 탈퇴 확인
	public boolean isDeleted() {
		return this.status == UserStatus.DELETED;
	}
}
