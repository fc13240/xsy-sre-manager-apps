package com.rkhd.sre.app.controller;

import com.rkhd.sre.app.entity.NodeInfo;
import com.rkhd.sre.app.entity.PathTreeRoot;
import com.rkhd.sre.app.entity.ZookeeperInfo;
import com.rkhd.sre.app.msg.ObjectRestResponse;
import com.rkhd.sre.app.service.ZookeeperService;
import com.rkhd.sre.app.support.zookeeper.ZookeeperConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ZookeeperController {
    @Autowired
    private ZookeeperService service;

    @GetMapping("/zkmain")
    public String zkmain(Model model) {
        model.addAttribute("list", service.getAll());
        return "zkmain";
    }

    @GetMapping("/zkAddForm")
    public String add(Model model) {
        ZookeeperInfo zookeeperInfo = new ZookeeperInfo();
        model.addAttribute("action", "/zkAddSave");
        model.addAttribute("zk", zookeeperInfo);
        return "addZkConnectionForm";
    }

    @GetMapping("/zkAddNode")
    public String addNode(Model model,String parentPath,String parentNodeId,String id) {
        ZookeeperInfo zookeeperInfo = new ZookeeperInfo();
        model.addAttribute("zookeeperId", id);
        model.addAttribute("parentPath", parentPath);
        model.addAttribute("parentNodeId", parentNodeId);
        return "addZkNode";
    }

    @PostMapping(value = "/addNodeSave", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ObjectRestResponse<Boolean> addNodeSave(@RequestParam(name = "id") String id,
                                            @RequestParam(name = "path") String path,
                                            @RequestParam(name = "data", required = false) String data) throws Exception {
        ObjectRestResponse<Boolean> response = new ObjectRestResponse<>();
        response.data(service.addNodeSave(id, path, data));
        return response;
    }

    @PostMapping(value = "/zkDeleteNode", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ObjectRestResponse<Boolean> zkDeleteNode(@RequestParam(name = "id") String id,
                                            @RequestParam(name = "path") String path) throws Exception {
        ObjectRestResponse<Boolean> response = new ObjectRestResponse<>();
        response.data(service.deleteNode(id, path));
        return response;
    }

    @GetMapping("/zkUpdateForm")
    public String zkUpdateForm(Model model, String id) {
        ZookeeperInfo zookeeperInfo = service.getByKey(id);
        model.addAttribute("action", "/zkUpdateSave");
        model.addAttribute("zk", zookeeperInfo);
        return "addZkConnectionForm";
    }

    @GetMapping("/zkManager")
    public String zkManager(Model model, String id) {
        model.addAttribute("id", id);
        return "zkManager";
    }

    @GetMapping("/tree")
    @ResponseBody
    public PathTreeRoot tree(Model model, @RequestParam(name = "id") String id, @RequestParam(name = "path", required = false, defaultValue = "/") String path) throws Exception {
        PathTreeRoot root = new PathTreeRoot();
        if (ZookeeperConstant.ROOT_PATH.equals(path) || ZookeeperConstant.EMPTY_STRING.equals(path)) {
            root.getRootNode().setNodes(service.buildPathTreeNodeListByPath(id, path));
        } else {
            root.removeRootNode();
            root.addAll(service.buildPathTreeNodeListByPath(id, path));
        }
        return root;
    }
    @GetMapping(value = "/node", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ObjectRestResponse<NodeInfo> queryNodeInfo(@RequestParam(name = "id") String id,
                                                      @RequestParam(name = "path") String path) throws Exception {
        ObjectRestResponse<NodeInfo> response = new ObjectRestResponse<>();
        response.setData(service.getNodeInfo(id, path));
        return response;
    }

    @PostMapping("/zkAddSave")
    @ResponseBody
    public ObjectRestResponse<ZookeeperInfo> save(Model model, ZookeeperInfo zookeeperInfo) {
        return service.add(zookeeperInfo);
    }

    @PostMapping("/zkUpdateSave")
    @ResponseBody
    public ObjectRestResponse<ZookeeperInfo> update(Model model, ZookeeperInfo zookeeperInfo, String updateId) {
        return service.update(updateId, zookeeperInfo);
    }

    @PostMapping("/zkDelete")
    @ResponseBody
    public ObjectRestResponse<ZookeeperInfo> zkDelete(Model model, String id) {
        return service.zkDelete(id);
    }

    @PostMapping("/zkReset")
    @ResponseBody
    public ObjectRestResponse<ZookeeperInfo> zkReset(Model model, String id) {
        return service.zkReset(id);
    }
}
