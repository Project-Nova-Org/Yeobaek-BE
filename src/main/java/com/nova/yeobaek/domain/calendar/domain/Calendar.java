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
@Table(name = "calendars",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_user_date",
			columnNames = {"user_id", "date"})
})
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
}
