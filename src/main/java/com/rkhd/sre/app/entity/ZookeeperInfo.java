package com.rkhd.sre.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.curator.framework.state.ConnectionState;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2018/4/24 22:29
 */
@Data
@ToString
@NoArgsConstructor
public class ZookeeperInfo {

	private String id;
	private String description;
	private String connectionString;
	private Integer sessionTimeout;
	private ConnectionState connectionState;
}
