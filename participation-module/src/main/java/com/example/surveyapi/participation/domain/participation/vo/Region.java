package com.example.surveyapi.participation.domain.participation.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Region {
	private String province;
	private String district;

	public static Region of(String province, String district) {
		Region region = new Region();
		region.province = province;
		region.district = district;

		return region;
	}
}
