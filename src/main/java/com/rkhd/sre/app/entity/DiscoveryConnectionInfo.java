package com.rkhd.sre.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class DiscoveryConnectionInfo {

    private String id;
    private String description;
    private String connectionString;
    private String connectionState;
    private String username;
    private String password;
}
