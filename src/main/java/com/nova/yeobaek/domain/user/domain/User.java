package com.nova.yeobaek.domain.user.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.user.domain.enums.Gender;
import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import com.nova.yeobaek.domain.user.domain.enums.Rank;
import com.nova.yeobaek.domain.user.domain.enums.UserStatus;
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
@Table(name = "users")
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

	private String nickname;

	@Column(columnDefinition = "TEXT")
	private String profileImageUrl;

	@Column(columnDefinition = "TEXT")
	private String bodyImageUrl;

	private float weight;

	private float height;

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
	private Long fittingCount;

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
}
