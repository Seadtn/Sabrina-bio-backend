package com.sabrinaBio.application.Modal.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BannerDTO {
	private Long id;
	private String name;
	private String nameFr;
	private String nameEng;
	private String image;
	private boolean promotion;
	private int soldRatio;
}
