package com.sjdddd.sojsandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sjdddd.sojsandbox.template.EnvCheckerAutoRegister;
import com.sjdddd.sojsandbox.template.EnvCheckerInstaller;

@SpringBootApplication
public class SojSandboxApplication {

    public static void main(String[] args) {
        initEnv();
        SpringApplication.run(SojSandboxApplication.class, args);
    }

    public static void initEnv() {
        EnvCheckerAutoRegister.registerAll();
        EnvCheckerInstaller.installAllInOrder();
    }

}
