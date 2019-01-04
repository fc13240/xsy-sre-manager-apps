package com.rkhd.sre.app.entity;

import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2018/4/24 23:12
 */
@Data
public class NodeInfo {

	private String id;
	private String path;
	private String data;
	private AclInfo aclMetadata;
	private ZookeeperStatMetadata statMetadata;
}
