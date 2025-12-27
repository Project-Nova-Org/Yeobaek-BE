package com.nova.yeobaek.domain.ootd.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.ootd.domain.enums.OOTDStatus;
import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.shared.ImageBackground;

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
@Table(name = "ootds")
public class OOTD extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String name;

	private String memo;

	@Column(nullable = false)
	private boolean favorite;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ImageBackground imageBackground;

	@Column(nullable = false)
	private String coverImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "DEFAULT 'NORMAL'")
	private OOTDStatus ootdStatus;

	@Column(nullable = false)
	private int itemCount;
}
