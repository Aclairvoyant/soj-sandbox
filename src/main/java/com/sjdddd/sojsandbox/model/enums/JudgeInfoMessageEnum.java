package com.sjdddd.sojsandbox.model.enums;


import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题结果信息枚举
 *

 */
public enum JudgeInfoMessageEnum {

    ACCEPTED("成功", "Accepted"),
    WRONG_ANSWER("答案错误", "Wrong Answer"),
    RUNTIME_ERROR("运行时错误", "Runtime Error"),
    TIME_LIMIT_EXCEEDED("超时", "Time Limit Exceeded"),
    MEMORY_LIMIT_EXCEEDED("超内存", "Memory Limit Exceeded"),
    OUTPUT_LIMIT_EXCEEDED("输出超限", "Output Limit Exceeded"),
    COMPILE_ERROR("编译错误", "Compile Error"),
    SYSTEM_ERROR("系统错误", "System Error"),
    UNKNOWN_ERROR("未知错误", "Unknown Error"),
    PENDING("等待中", "Pending"),
    DANGEROUS_OPERATION("危险操作", "Dangerous Operation");

    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
