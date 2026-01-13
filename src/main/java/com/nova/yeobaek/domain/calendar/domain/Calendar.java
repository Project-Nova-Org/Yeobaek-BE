package com.nova.yeobaek.domain.calendar.domain;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(
		name = "calendars",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_user_date",
						columnNames = {"user_id", "date"}
				)
		}
)
public class Calendar extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String ootdImageUrl;

	@Column(columnDefinition = "TEXT")
	private String customImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR DEFAULT 'OOTD'")
	private Thumbnail thumbnail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ootd_id")
	private OOTD ootd;

	/**
	 * 대표 이미지를 OOTD로 설정 (CUSTOM 있으면 제거해서 썸네일이 OOTD로 나오게)
	 */
	public void connectOotd(OOTD ootd) {
		this.ootd = ootd;
		this.ootdImageUrl = ootd.getImageUrl();   // CalendarConverter가 이 값을 썸네일로 사용
		this.customImageUrl = null;               // "대표 이미지를 OOTD로" 명세 반영
		this.thumbnail = Thumbnail.OOTD;
	}

	/**
	 * OOTD 연결 해제 (엔트리 row 삭제 아님)
	 * ootdImageUrl 컬럼이 NOT NULL이라 null로는 안 만듦.
	 */
	public void disconnectOotd() {
		this.ootd = null;
		// 썸네일은 customImageUrl이 있으면 CUSTOM로, 없으면 "기록 없음"처럼 내려가게 됨(Converter 로직)
		// ootdImageUrl은 NOT NULL 컬럼이라 유지
	}
}
