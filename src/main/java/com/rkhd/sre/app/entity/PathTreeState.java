package com.rkhd.sre.app.entity;

import lombok.Data;


@Data
public class PathTreeState {

	private Boolean checked;
	private Boolean disabled;
	private Boolean expanded;
	private Boolean selected;
}
