package com.rkhd.sre.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.sre.app.entity.DiscoveryConnectionInfo;
import com.rkhd.sre.app.entity.ServiceInstance;
import com.rkhd.sre.app.msg.ObjectRestResponse;
import com.rkhd.sre.app.support.discovery.DiscoveryConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DiscoveryService {

    @Autowired
    private DiscoveryConnectionManager manager;

    @Autowired
    private RestTemplate restTemplate;

    public List<DiscoveryConnectionInfo> getAll() {
        return manager.findAll();
    }

    public List<ServiceInstance> getApps(String id) {
        List<ServiceInstance> list = new ArrayList<>();
        DiscoveryConnectionInfo discoveryConnectionInfo = manager.getByKey(id);
        HttpHeaders headers = new HttpHeaders();//header参数
        headers.add("Authorization", "Basic " + Base64.encodeBase64String((discoveryConnectionInfo.getUsername() + ":" + discoveryConnectionInfo.getPassword()).getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Acept", "application/json");

        String url = new StringBuffer().append("http://").append(discoveryConnectionInfo.getConnectionString()).append("/eureka/apps").toString();

        HttpEntity<JSONObject> request = new HttpEntity<>(null, headers); //组装
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (RestClientException e) {
            log.error(e.getMessage());
        }
        if (null == response) {
            return list;
        }
        JSONObject obj = JSON.parseObject(response.getBody());
        if (obj != null) {
            JSONObject applications = obj.getJSONObject("applications");
            if (applications != null) {
                JSONArray application = applications.getJSONArray("application");
                for (int i = 0; application != null && i < application.size(); i++) {
                    JSONObject object = application.getJSONObject(i);
                    if (object != null) {
                        JSONArray array = object.getJSONArray("instance");
                        for (int j = 0; array != null & j < array.size(); j++) {
                            ServiceInstance serviceInstance = new ServiceInstance();
                            JSONObject o = array.getJSONObject(j);
                            serviceInstance.setStatus(o.getString("status"));
                            serviceInstance.setHost(o.getString("ipAddr"));
                            serviceInstance.setInstanceId(o.getString("instanceId"));
                            serviceInstance.setName(object.getString("name"));
                            serviceInstance.setApp(o.getString("app"));
                            JSONObject portObject = o.getJSONObject("port");
                            if (portObject != null) {
                                String portString = portObject.getString("$");
                                if (StringUtils.isNotEmpty(portString)) {
                                    serviceInstance.setPort(Integer.parseInt(portString));
                                }
                            }
                            list.add(serviceInstance);
                        }
                    }
                }
            }
        }
        return list;
    }

    public ObjectRestResponse<Boolean> down(String id, ServiceInstance serviceInstance) {
        DiscoveryConnectionInfo discoveryConnectionInfo = manager.getByKey(id);
        HttpHeaders headers = new HttpHeaders();//header参数
        headers.add("Authorization", "Basic " + Base64.encodeBase64String((discoveryConnectionInfo.getUsername() + ":" + discoveryConnectionInfo.getPassword()).getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Acept", "application/json");

        String url = new StringBuffer().append("http://").append(discoveryConnectionInfo.getConnectionString()).append("/eureka/apps/").append(serviceInstance.getApp())
                .append("/").append(serviceInstance.getInstanceId()).append("/status?value=OUT_OF_SERVICE").toString();
        HttpEntity<JSONObject> request = new HttpEntity<>(null, headers); //组装
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        } catch (RestClientException e) {
            log.error(e.getMessage());
        }

        JSONObject obj = JSON.parseObject(response.getBody());
        return ObjectRestResponse.ok(true);

    }

    public ObjectRestResponse<Boolean> reset(String id, ServiceInstance serviceInstance) {
        DiscoveryConnectionInfo discoveryConnectionInfo = manager.getByKey(id);
        HttpHeaders headers = new HttpHeaders();//header参数
        headers.add("Authorization", "Basic " + Base64.encodeBase64String((discoveryConnectionInfo.getUsername() + ":" + discoveryConnectionInfo.getPassword()).getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Acept", "application/json");

        String url = new StringBuffer().append("http://").append(discoveryConnectionInfo.getConnectionString()).append("/eureka/apps/").append(serviceInstance.getApp())
                .append("/").append(serviceInstance.getInstanceId()).append("/status?value=UP").toString();
        HttpEntity<JSONObject> request = new HttpEntity<>(null, headers); //组装
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        } catch (RestClientException e) {
            log.error(e.getMessage());
        }

        JSONObject obj = JSON.parseObject(response.getBody());
        return ObjectRestResponse.ok(true);

    }

    public ObjectRestResponse<DiscoveryConnectionInfo> add(DiscoveryConnectionInfo discoveryConnectionInfo) {
        if (StringUtils.isEmpty(discoveryConnectionInfo.getConnectionString())) {
            ObjectRestResponse result = new ObjectRestResponse();
            result.setStatus(600);
            result.setMessage("参数错误");
            return result;
        }
        DiscoveryConnectionInfo discovery = manager.add(discoveryConnectionInfo);
        return ObjectRestResponse.ok();
    }

    public ObjectRestResponse<DiscoveryConnectionInfo> update(String updateId, DiscoveryConnectionInfo discoveryConnectionInfo) {
        if (StringUtils.isEmpty(updateId) || StringUtils.isEmpty(discoveryConnectionInfo.getConnectionString())) {
            ObjectRestResponse result = new ObjectRestResponse();
            result.setStatus(600);
            result.setMessage("参数错误");
            return result;
        }
        DiscoveryConnectionInfo discovery = manager.update(updateId, discoveryConnectionInfo);
        return ObjectRestResponse.ok();
    }

    public DiscoveryConnectionInfo getByKey(String key) {
        return manager.getByKey(key);
    }

    public ObjectRestResponse<DiscoveryConnectionInfo> zkDelete(String id) {
        if (StringUtils.isEmpty(id)) {
            ObjectRestResponse result = new ObjectRestResponse();
            result.setStatus(600);
            result.setMessage("参数错误");
            return result;
        }
        DiscoveryConnectionInfo zk = manager.delete(id);
        return ObjectRestResponse.ok();
    }
}
