package com.sjdddd.sojsandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 题目判题信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存 KB
     */
    private Long memory;

    /**
     * 消耗时间 ms
     */
    private Long time;

}
