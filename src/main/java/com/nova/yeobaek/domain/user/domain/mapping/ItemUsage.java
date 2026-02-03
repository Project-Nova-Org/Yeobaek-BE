package com.nova.yeobaek.domain.user.domain.mapping;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.user.domain.User;

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
@Table(name = "item_usages",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_user_item",
						columnNames = {"user_id", "item_id"})
		})
public class ItemUsage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int useCount;

	private LocalDate lastUsedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	// =========================
	// ✅ business methods
	// =========================

	/** 사용 횟수 +1 + 마지막 사용일 갱신 */
	public void increase(LocalDate usedDate) {
		this.useCount += 1;
		this.lastUsedDate = usedDate;
	}

	/** 사용 횟수 -1 (0 미만 방지), lastUsedDate는 재계산 비용 커서 유지 */
	public void decrease() {
		this.useCount = Math.max(0, this.useCount - 1);
	}

    /**
     * OOTD 수정 diff 반영용
     * - 특정 아이템 사용 횟수를 N회 증가
     */
    public void increase(int count) {
        if (count <= 0) return;
        this.useCount += count;
    }

    /**
     * OOTD 수정 diff 반영용
     * - 특정 아이템 사용 횟수를 N회 감소 (0 미만 방지)
     */
    public void decrease(int count) {
        if (count <= 0) return;
        this.useCount = Math.max(0, this.useCount - count);
    }
}
