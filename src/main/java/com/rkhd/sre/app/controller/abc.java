package com.rkhd.sre.app.controller;

import com.google.common.collect.Maps;

import java.util.Map;

public class abc {
    private static final Map<String, Integer> KEY_COUNT = Maps.newConcurrentMap();
    public static void main(String[] args) {
        for (int i = 1; i < 3; i++) {

        int count = KEY_COUNT.computeIfAbsent("669", s -> 0);
        KEY_COUNT.computeIfPresent("669", (k, v) -> v+1);
        }
        System.out.println(KEY_COUNT);
    }
}
