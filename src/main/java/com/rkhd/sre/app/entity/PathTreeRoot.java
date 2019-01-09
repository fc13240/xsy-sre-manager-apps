package com.rkhd.sre.app.entity;


import com.rkhd.sre.app.support.zookeeper.ZookeeperConstant;

import java.util.ArrayList;


public class PathTreeRoot extends ArrayList<PathTreeNode> {

	public PathTreeRoot() {
		PathTreeNode pathTreeNode = new PathTreeNode();
		pathTreeNode.setParentPath(ZookeeperConstant.EMPTY_STRING);
		pathTreeNode.setPath(ZookeeperConstant.ROOT_PATH);
		pathTreeNode.setFullPath(ZookeeperConstant.ROOT_PATH);
		pathTreeNode.setText(ZookeeperConstant.ROOT_PATH);
		this.add(pathTreeNode);
	}

	public void removeRootNode() {
		this.remove(0);
	}

	public PathTreeNode getRootNode(){
		return this.get(0);
	}
}
