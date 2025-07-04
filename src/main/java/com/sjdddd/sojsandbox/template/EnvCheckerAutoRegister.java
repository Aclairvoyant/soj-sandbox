package com.sjdddd.sojsandbox.template;

import com.sjdddd.sojsandbox.template.c.CEnvChecker;
import com.sjdddd.sojsandbox.template.cpp.CppEnvChecker;
import com.sjdddd.sojsandbox.template.go.GoEnvChecker;
import com.sjdddd.sojsandbox.template.java.JavaEnvChecker;
import com.sjdddd.sojsandbox.template.python3.Python3EnvChecker;

public class EnvCheckerAutoRegister {
    public static void registerAll() {
        EnvCheckerRegistry.registerChecker(new CEnvChecker());
        EnvCheckerRegistry.registerChecker(new CppEnvChecker());
        EnvCheckerRegistry.registerChecker(new GoEnvChecker());
        EnvCheckerRegistry.registerChecker(new JavaEnvChecker());
        EnvCheckerRegistry.registerChecker(new Python3EnvChecker());
    }
} 