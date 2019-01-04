package com.rkhd.sre.app.entity;

import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2018/4/24 22:52
 */
@Data
public class ZookeeperAclMetadata {

	private String scheme;
	private String id;
	private String perms;
}
