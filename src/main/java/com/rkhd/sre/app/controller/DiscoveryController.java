package com.rkhd.sre.app.controller;

import com.rkhd.sre.app.entity.DiscoveryConnectionInfo;
import com.rkhd.sre.app.entity.ServiceInstance;
import com.rkhd.sre.app.msg.ObjectRestResponse;
import com.rkhd.sre.app.service.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DiscoveryController {

    @Autowired
    private DiscoveryService service;

    @GetMapping("/discoverymain")
    public String discoverymain(Model model) {
        model.addAttribute("list", service.getAll());
        return "discoverymain";
    }
    @GetMapping("/discoveryManager")
    public String discoveryManager(Model model, String id) {
        model.addAttribute("list", service.getApps(id));
        model.addAttribute("id", id);
        return "discoveryManager";
    }

    @GetMapping("/discoveryApps")
    @ResponseBody
    public List<ServiceInstance> discoveryApps(Model model, String id) {
        return service.getApps(id);
    }

    @RequestMapping("/discoveryManager/down/{id}")
    @ResponseBody
    public ObjectRestResponse<Boolean> discoveryManagerDown(@PathVariable("id") String id, @RequestBody ServiceInstance serviceInstance) {
        return service.down(id,serviceInstance);
    }

    @PostMapping("/discoveryManager/reset/{id}")
    @ResponseBody
    public ObjectRestResponse<Boolean> discoveryManagerReset(@PathVariable("id") String id, @RequestBody ServiceInstance serviceInstance) {
        return service.reset(id,serviceInstance);
    }


    @GetMapping("/discoveryAddForm")
    public String add(Model model) {
        DiscoveryConnectionInfo discoveryConnectionInfo = new DiscoveryConnectionInfo();
        model.addAttribute("action", "/discoveryAddSave");
        model.addAttribute("discovery", discoveryConnectionInfo);
        return "addDiscoveryConnectionForm";
    }
    @PostMapping("/discoveryAddSave")
    @ResponseBody
    public ObjectRestResponse<DiscoveryConnectionInfo> discoveryAddSave(Model model, DiscoveryConnectionInfo discoveryConnectionInfo) {
        return service.add(discoveryConnectionInfo);
    }

    @GetMapping("/discoveryUpdateForm")
    public String zkUpdateForm(Model model, String id) {
        DiscoveryConnectionInfo discoveryConnectionInfo = service.getByKey(id);
        model.addAttribute("action", "/discoveryUpdateSave");
        model.addAttribute("discovery", discoveryConnectionInfo);
        return "addDiscoveryConnectionForm";
    }

    @PostMapping("/discoveryUpdateSave")
    @ResponseBody
    public ObjectRestResponse<DiscoveryConnectionInfo> discoveryUpdateSave(Model model, DiscoveryConnectionInfo discoveryConnectionInfo, String updateId) {
        return service.update(updateId, discoveryConnectionInfo);
    }

    @PostMapping("/discoveryDelete")
    @ResponseBody
    public ObjectRestResponse<DiscoveryConnectionInfo> discoveryDelete(Model model, String id) {
        return service.zkDelete(id);
    }

}
