package com.nova.yeobaek.domain.user.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.user.domain.enums.Gender;
import com.nova.yeobaek.domain.user.domain.enums.OAuthProvider;
import com.nova.yeobaek.domain.user.domain.enums.Rank;
import com.nova.yeobaek.domain.user.domain.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OAuthProvider oAuthProvider;

	@Column(nullable = false)
	private String oAuthId;

	private String email;

	private String nickname;

	private String profileImageUrl;

	private String bodyImageUrl;

	private float weight;

	private float height;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "DEFAULT 'LV1'")
	private Rank rank;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "DEFAULT 'ACTIVE'")
	private UserStatus userStatus;

	private LocalDateTime inactiveDate;

	@Enumerated(EnumType.STRING)
	private Gender gender;
}
