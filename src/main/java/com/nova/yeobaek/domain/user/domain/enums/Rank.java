package com.nova.yeobaek.domain.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Rank {
	LV1("코디 새싹"), LV2("아이템 수집가"), LV3("룩북 메이커"), LV4("패셔니스타"), LV5("여백의 마스터");

	private final String name;
}
