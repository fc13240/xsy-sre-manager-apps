package com.rkhd.sre.app.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInstance implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String host;
    private int port;
    private String status;
    private String instanceId;
    private String app;


    public int getId() {
        return (getName() + getHost() + getPort()).hashCode();
    }

}
