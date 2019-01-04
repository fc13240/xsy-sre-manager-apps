package com.rkhd.sre.app.entity;

import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2018/4/29 19:02
 */
@Data
public class AclInfo {

	private String scheme;
	private String id;
	private String perms;
}
