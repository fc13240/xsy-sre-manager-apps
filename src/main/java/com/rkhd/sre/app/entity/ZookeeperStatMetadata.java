package com.rkhd.sre.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2018/4/24 22:41
 */
@Data
@NoArgsConstructor
public class ZookeeperStatMetadata {

	private Long czxid;
	private Long mzxid;
	private Long ctime;
	private Long mtime;
	private Integer version;
	private Integer cversion;
	private Integer aversion;
	private Long ephemeralOwner;
	private Integer dataLength;
	private Integer numChildren;
	private Long pzxid;
}
