package com.sjdddd.sojsandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 沈佳栋
 * @Description: TODO
 * @DateTime: 2024/3/7 21:46
 **/
@RestController("/")
public class MainController {

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
