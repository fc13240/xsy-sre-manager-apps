package com.rkhd.sre.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.curator.framework.state.ConnectionState;


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
