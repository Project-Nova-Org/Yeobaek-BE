package com.nova.yeobaek.domain.ootd.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.ootd.domain.enums.OOTDStatus;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.shared.BaseEntity;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;

import jakarta.persistence.CascadeType;
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
@Table(name = "ootds")
public class OOTD extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	private String memo;

	@Builder.Default
	private boolean favorite = false;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ImageBackgroundColor imageBackgroundColor;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String imageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR DEFAULT 'NORMAL'")
	private OOTDStatus status;

	@Builder.Default
	@Column(nullable = false)
	private Long changeItemCount = 0L; //초기값 설정

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tpo_id")
	private TPO tpo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "style_id")
	private Style style;

	@OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Calendar> calendarList = new ArrayList<>();

	@OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
	@Builder.Default
	private List<OOTDItem> ootdItemList = new ArrayList<>();

	/** OOTD 수정 */
    // 이름 수정
    public void updateName(String name) {
        this.name = name;
    }

    // 메모 수정
    public void updateMemo(String memo) {
        this.memo = memo;
    }

    //즐겨찾기 수정
    public void updateFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    //TPO 수정
    public void updateTpo(TPO tpo) {
        this.tpo = tpo;
    }

    //Style 수정
    public void updateStyle(Style style) {
        this.style = style;
    }

    //이미지 배경 색상 수정
    public void updateImageBackground(ImageBackgroundColor backgroundColor) {
        this.imageBackgroundColor = backgroundColor;
    }

    //아이템 변경 횟수 증가
    public void increaseChangeItemCount() {
        this.changeItemCount++;
    }

    //OOTD 상태 변경
    public void changeStatus(OOTDStatus status) {
        this.status = status;
    }

	//OOTD 즐겨찾기
	public void toggleFavorite() {
		this.favorite = !this.favorite;
	}
}
