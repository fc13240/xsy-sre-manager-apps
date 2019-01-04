package com.rkhd.sre.app.service;

import com.google.common.collect.Lists;
import com.rkhd.sre.app.entity.*;
import com.rkhd.sre.app.msg.ObjectRestResponse;
import com.rkhd.sre.app.support.CuratorFrameworkManager;
import com.rkhd.sre.app.support.ZookeeperConnectionManager;
import com.rkhd.sre.app.support.ZookeeperConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Service
public class ZookeeperService {


    @Autowired
    private ZookeeperConnectionManager manager;
    @Autowired
    private CuratorFrameworkManager curatorFrameworkManager;

    public List<ZookeeperInfo> getAll() {
        return manager.findAll();
    }

    public ZookeeperInfo getByKey(String key) {
        return manager.getByKey(key);
    }

    public List<PathTreeNode> buildPathTreeNodeListByPath(String id, String path) throws Exception {
        if (!path.startsWith(ZookeeperConstant.ROOT_PATH)) {
            path = String.format("%s%s", ZookeeperConstant.ROOT_PATH, path);
        }
        CuratorFramework curator = getExistCurator(id);
        List<String> children = curator.getChildren().forPath(path);
        List<PathTreeNode> pathTreeNodeList = new ArrayList<>();
        if (!children.isEmpty()) {
            for (String child : children) {
                PathTreeNode node = new PathTreeNode();
                node.setPath(child);
                node.setHref(String.format("%s%s", "#", node.getPath()));
                node.setText(node.getPath());
                node.setFullPath(ZookeeperConstant.ROOT_PATH.equals(path) ? String.format("%s%s", path, child)
                        : String.format("%s%s%s", path, ZookeeperConstant.ROOT_PATH, child));
                node.setParentPath(path);
                List<PathTreeNode> pathTreeNodes = buildPathTreeNodeListByPath(id, node.getFullPath());
                if (null == pathTreeNodes || pathTreeNodes.isEmpty()) {
                    PathTreeState state = new PathTreeState();
                    state.setExpanded(Boolean.FALSE);
                    node.setState(state);
                } else {
                    node.setNodes(pathTreeNodes);
                }
                pathTreeNodeList.add(node);
            }
        }
        return pathTreeNodeList;
    }

    public NodeInfo getNodeInfo(String id, String path) throws Exception {
        CuratorFramework curator = getExistCurator(id);
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setId(id);
        nodeInfo.setPath(path);
        nodeInfo.setStatMetadata(collectStatMetadata(curator, path));
        AclInfo aclInfo = new AclInfo();
        StringBuilder ids = new StringBuilder();
        StringBuilder schemas = new StringBuilder();
        StringBuilder perms = new StringBuilder();
        List<ZookeeperAclMetadata> aclMetadata = collectAclMetadata(curator, path);
        for (ZookeeperAclMetadata metadata : aclMetadata) {
            ids.append(metadata.getId()).append(";");
            schemas.append(metadata.getScheme()).append(";");
            perms.append(metadata.getPerms()).append(";");
        }
        if (ids.length() > 0) {
            aclInfo.setId(ids.substring(0, ids.lastIndexOf(";")));
        }
        if (perms.length() > 0) {
            aclInfo.setPerms(perms.substring(0, perms.lastIndexOf(";")));
        }
        if (schemas.length() > 0) {
            aclInfo.setScheme(schemas.substring(0, schemas.lastIndexOf(";")));
        }
        nodeInfo.setAclMetadata(aclInfo);
        nodeInfo.setData(getNodeData(curator, path));
        return nodeInfo;
    }
    private List<ZookeeperAclMetadata> collectAclMetadata(CuratorFramework curator, String path) throws Exception {
        List<ZookeeperAclMetadata> aclMetadata = Lists.newArrayList();
        if (checkNodeExist(curator, path)) {
            List<ACL> acls = curator.getACL().forPath(path);
            if (null != acls && !acls.isEmpty()) {
                for (ACL acl : acls) {
                    ZookeeperAclMetadata data = new ZookeeperAclMetadata();
                    aclMetadata.add(data);
                    data.setId(acl.getId().getId());
                    data.setScheme(acl.getId().getScheme());
                    StringBuilder permsBuilder = new StringBuilder();
                    int perms = acl.getPerms();
                    if ((perms & ZooDefs.Perms.READ) == ZooDefs.Perms.READ) {
                        permsBuilder.append("Read, ");
                    }
                    if ((perms & ZooDefs.Perms.WRITE) == ZooDefs.Perms.WRITE) {
                        permsBuilder.append("Write, ");
                    }
                    if ((perms & ZooDefs.Perms.CREATE) == ZooDefs.Perms.CREATE) {
                        permsBuilder.append("Create, ");
                    }
                    if ((perms & ZooDefs.Perms.DELETE) == ZooDefs.Perms.DELETE) {
                        permsBuilder.append("Delete, ");
                    }
                    if ((perms & ZooDefs.Perms.ADMIN) == ZooDefs.Perms.ADMIN) {
                        permsBuilder.append("Admin, ");
                    }
                    if (permsBuilder.length() > 0) {
                        data.setPerms(permsBuilder.substring(0, permsBuilder.lastIndexOf(",")));
                    }
                }
            }
        }
        return aclMetadata;
    }
    private boolean checkNodeExist(CuratorFramework curator, String path) throws Exception {
        return null != curator.checkExists().forPath(path);
    }

    private String getNodeData(CuratorFramework curator, String path) throws Exception {
        byte[] bytes = curator.getData().forPath(path);
        return new String(bytes, Charset.forName(ZookeeperConstant.CHARSET));
    }
    private ZookeeperStatMetadata collectStatMetadata(CuratorFramework curator, String path) throws Exception {
        ZookeeperStatMetadata metadata = new ZookeeperStatMetadata();
        Stat stat = curator.checkExists().forPath(path);
        if (null != stat) {
            metadata.setCzxid(stat.getCzxid());
            metadata.setCtime(stat.getCtime());
            metadata.setMzxid(stat.getMzxid());
            metadata.setMtime(stat.getMtime());
            metadata.setVersion(stat.getVersion());
            metadata.setCversion(stat.getCversion());
            metadata.setAversion(stat.getAversion());
            metadata.setEphemeralOwner(stat.getEphemeralOwner());
            metadata.setDataLength(stat.getDataLength());
            metadata.setNumChildren(stat.getNumChildren());
            metadata.setPzxid(stat.getPzxid());
        }
        return metadata;
    }
    private CuratorFramework getExistCurator(String id) {
        CuratorFramework curatorFramework = curatorFrameworkManager.getCuratorFramework(id);
        Assert.notNull(curatorFramework, "CuratorFramework must not be null for id = " + id);
        return curatorFramework;
    }

    public ObjectRestResponse<ZookeeperInfo> add(ZookeeperInfo zookeeperInfo) {
        if (StringUtils.isEmpty(zookeeperInfo.getConnectionString()) || zookeeperInfo.getSessionTimeout() == null) {
            ObjectRestResponse result = new ObjectRestResponse();
            result.setStatus(600);
            result.setMessage("参数错误");
            return result;
        }
        ZookeeperInfo zk = manager.add(zookeeperInfo);
        return ObjectRestResponse.ok();
    }

    public ObjectRestResponse<ZookeeperInfo> update(String updateId,ZookeeperInfo zookeeperInfo) {
        if (StringUtils.isEmpty(updateId) || StringUtils.isEmpty(zookeeperInfo.getConnectionString()) || zookeeperInfo.getSessionTimeout() == null) {
            ObjectRestResponse result = new ObjectRestResponse();
            result.setStatus(600);
            result.setMessage("参数错误");
            return result;
        }
        ZookeeperInfo zk = manager.update(updateId,zookeeperInfo);
        return ObjectRestResponse.ok();
    }

    public ObjectRestResponse<ZookeeperInfo> zkDelete(String id) {
        if (StringUtils.isEmpty(id)) {
            ObjectRestResponse result = new ObjectRestResponse();
            result.setStatus(600);
            result.setMessage("参数错误");
            return result;
        }
        ZookeeperInfo zk = manager.delete(id);
        return ObjectRestResponse.ok();
    }

    public ObjectRestResponse<ZookeeperInfo> zkReset(String id) {
        if (StringUtils.isEmpty(id)) {
            ObjectRestResponse result = new ObjectRestResponse();
            result.setStatus(600);
            result.setMessage("参数错误");
            return result;
        }
        manager.reset(id);
        return ObjectRestResponse.ok();
    }


}
